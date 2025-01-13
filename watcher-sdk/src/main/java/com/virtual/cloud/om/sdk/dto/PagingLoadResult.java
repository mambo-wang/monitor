package com.virtual.cloud.om.sdk.dto;

/**
 * A <code>LoadResult</code> for paging loaders.
 *
 * @param <Data> the data type
 */
public interface PagingLoadResult<Data> extends ListLoadResult<Data> {

  /**
   * Returns the current offset of the results.
   *
   * @return the offset
   */
  int getTotalPages();

  /**
   * Returns the total count. This value will not equal the number of records
   * being returned when paging is used.
   *
   * @return the total count
   */
  int getTotalLength();

  /**
   * Sets the offset.
   *
   * @param totalPages the offset
   */
  void setTotalPages(int totalPages);

  /**
   * Sets the total length.
   *
   * @param totalLength the total length
   */
  void setTotalLength(int totalLength);
}

