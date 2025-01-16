
/** 设置和拉取tab数据的主方法 */
const tabsHook = {
  setItem: function(arr: object[]) {
    sessionStorage.setItem('tabs', JSON.stringify(arr))
  },
  getItem: function() {
    return JSON.parse(sessionStorage.getItem('tabs') || '[]')
  }
}
export default tabsHook
