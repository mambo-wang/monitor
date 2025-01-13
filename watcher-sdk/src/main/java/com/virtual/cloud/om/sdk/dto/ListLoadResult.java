package com.virtual.cloud.om.sdk.dto;

import java.util.List;

/**
 * Load result interface for list based load results.
 *
 * @param <Data> the result data type
 */
public interface ListLoadResult<Data> {

  /**
   * Returns the remote data.
   *
   * @return the data
   */
  List<Data> getData();

}
