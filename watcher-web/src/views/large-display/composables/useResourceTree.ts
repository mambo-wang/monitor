/**
 * useResourceTree - Resource tree state management composable
 * Manages the hierarchical resource selection (Platform → Cluster → Host → VM)
 */
import { ref, computed, reactive } from 'vue';
import type {
  PlatformDTO,
  ResourceItemDTO,
  ResourceTreeNode,
  ResourceType,
} from '@/views/large-display/types';
import {
  getPlatformList,
  getPlatformChildren,
  getClusterHosts,
  getHostVMs,
} from '@/api/largeDisplay';

/**
 * Resource tree state
 */
interface ResourceTreeState {
  platforms: PlatformDTO[];
  tree: ResourceTreeNode[];
  selectedNode: ResourceTreeNode | null;
  expandedNodes: Set<number>;
  loadingNodes: Set<number>;
  error: string | null;
}

export function useResourceTree() {
  const state = reactive<ResourceTreeState>({
    platforms: [],
    tree: [],
    selectedNode: null,
    expandedNodes: new Set(),
    loadingNodes: new Set(),
    error: null,
  });

  /**
   * Load initial platform list
   */
  async function loadPlatforms() {
    try {
      state.error = null;
      const platforms = await getPlatformList();
      state.platforms = platforms;
      
      // Build tree structure
      state.tree = platforms.map((platform: PlatformDTO) => ({
        id: platform.id,
        name: platform.name,
        type: 'CLUSTER' as ResourceType, // Platform acts as root container
        status: platform.status,
        children: [],
        expanded: false,
        selected: false,
        loading: false,
        level: 0,
      }));
    } catch (err) {
      state.error = err instanceof Error ? err.message : 'Failed to load platforms';
      console.error('[useResourceTree] loadPlatforms error:', err);
    }
  }

  /**
   * Load children for a node
   */
  async function loadChildren(node: ResourceTreeNode): Promise<ResourceItemDTO[]> {
    if (state.loadingNodes.has(node.id)) {
      return [];
    }
    
    try {
      state.loadingNodes.add(node.id);
      state.error = null;
      
      let children: ResourceItemDTO[] = [];
      
      switch (node.type) {
        case 'CLUSTER':
          if (node.level === 0) {
            // Platform level - load desktop pools, terminals, clusters
            children = await getPlatformChildren(node.id);
          } else {
            // Cluster level - load hosts
            children = await getClusterHosts(node.id);
          }
          break;
        case 'HOST':
          // Host level - load VMs
          children = await getHostVMs(node.id);
          break;
        default:
          // Terminal, Desktop Pool, VM - no children
          return [];
      }
      
      return children;
    } catch (err) {
      state.error = err instanceof Error ? err.message : 'Failed to load children';
      console.error('[useResourceTree] loadChildren error:', err);
      return [];
    } finally {
      state.loadingNodes.delete(node.id);
    }
  }

  /**
   * Toggle node expansion and load children if needed
   */
  async function toggleExpand(nodeId: number) {
    const node = findNodeById(state.tree, nodeId);
    if (!node) return;
    
    if (node.expanded) {
      // Collapse
      node.expanded = false;
      state.expandedNodes.delete(nodeId);
    } else {
      // Expand and load children if not loaded
      if (node.children.length === 0) {
        const children = await loadChildren(node);
        node.children = children.map(child => ({
          id: child.id,
          name: child.name,
          type: child.type,
          status: child.status,
          ipAddress: child.ipAddress,
          metrics: child.metrics,
          children: [],
          expanded: false,
          selected: false,
          loading: false,
          level: node.level + 1,
        }));
      }
      node.expanded = true;
      state.expandedNodes.add(nodeId);
    }
  }

  /**
   * Select a node
   */
  function selectNode(nodeId: number) {
    // Deselect all
    deselectAll(state.tree);
    
    const node = findNodeById(state.tree, nodeId);
    if (node) {
      node.selected = true;
      state.selectedNode = node;
    }
  }

  /**
   * Deselect all nodes
   */
  function deselectAll(nodes: ResourceTreeNode[]) {
    for (const node of nodes) {
      node.selected = false;
      deselectAll(node.children);
    }
  }

  /**
   * Find node by ID recursively
   */
  function findNodeById(nodes: ResourceTreeNode[], id: number): ResourceTreeNode | null {
    for (const node of nodes) {
      if (node.id === id) return node;
      const found = findNodeById(node.children, id);
      if (found) return found;
    }
    return null;
  }

  /**
   * Get selected resource for monitoring
   */
  const selectedResource = computed(() => {
    if (!state.selectedNode) return null;
    
    // Only HOST and VM have monitoring data
    if (state.selectedNode.type === 'HOST' || state.selectedNode.type === 'VM') {
      return {
        id: state.selectedNode.id,
        name: state.selectedNode.name,
        type: state.selectedNode.type,
        ipAddress: state.selectedNode.ipAddress,
      };
    }
    return null;
  });

  /**
   * Check if a node has children (for expand icon)
   */
  function hasChildren(node: ResourceTreeNode): boolean {
    // Cluster, Host always have children potential
    // VM, Terminal, Desktop Pool don't
    return node.type === 'CLUSTER' || node.type === 'HOST';
  }

  /**
   * Check if node is loading
   */
  function isLoading(nodeId: number): boolean {
    return state.loadingNodes.has(nodeId);
  }

  /**
   * Reset tree state
   */
  function reset() {
    state.platforms = [];
    state.tree = [];
    state.selectedNode = null;
    state.expandedNodes.clear();
    state.loadingNodes.clear();
    state.error = null;
  }

  return {
    // State
    platforms: computed(() => state.platforms),
    tree: computed(() => state.tree),
    selectedNode: computed(() => state.selectedNode),
    selectedResource,
    error: computed(() => state.error),
    
    // Actions
    loadPlatforms,
    toggleExpand,
    selectNode,
    reset,
    
    // Utilities
    findNodeById: (id: number) => findNodeById(state.tree, id),
    hasChildren,
    isLoading,
  };
}
