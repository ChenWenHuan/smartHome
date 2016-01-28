package com.smarthome.client2.familySchool.view;

/**
 * The listener interface for receiving abOnListView events. The class that is
 * interested in processing a abOnListView event implements this interface, and
 * the object created with that class is registered with a component using the
 * component's <code>addAbOnListViewListener<code> method. When
 * the abOnListView event occurs, that object's appropriate
 * method is invoked.
 * 
 */
public interface AbOnListViewListener {

	/**
	 * On refresh.
	 */
	void onRefresh();

	/**
	 * On load more.
	 */
	void onLoadMore();
}
