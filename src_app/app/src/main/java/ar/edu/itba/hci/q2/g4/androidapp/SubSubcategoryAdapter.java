package ar.edu.itba.hci.q2.g4.androidapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SubSubcategoryAdapter extends RecyclerView.Adapter<SubSubcategoryAdapter.ViewHolder> {
	// Provide a reference to the views for each data item
	// Complex data items may need more than one view per item, and
	// you provide access to all the views for a data item in a view holder
	public static class ViewHolder extends RecyclerView.ViewHolder {

		public ViewHolder(View itemView) {
			super(itemView);
		}
	}

	public static class ItemViewHolder extends ViewHolder {

		private static final String TAG_ID = "id";
		private static final String TAG_TITLE = "title";
		private static final String TAG_COMES_FROM_CATEGORY_SEARCH = "category_search";

		CardView cv;
		TextView productName;
		TextView productBrand;
		TextView productPrice;
		ImageView productPhoto;
		int productId;

		public ItemViewHolder(View v, final Context context) {
			super(v);
			// Define click listener for the ViewHolder's View.
			v.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, ProductDetailActivity.class);
					intent.putExtra(TAG_TITLE, productName.getText());
					intent.putExtra(TAG_ID, productId);
					intent.putExtra(TAG_COMES_FROM_CATEGORY_SEARCH, true);
					context.startActivity(intent);
				}
			});
			cv = (CardView)itemView.findViewById(R.id.cv);
			productName = (TextView)itemView.findViewById(R.id.product_name);
			productBrand = (TextView)itemView.findViewById(R.id.product_brand);
			productPrice = (TextView)itemView.findViewById(R.id.product_price);
			productPhoto = (ImageView)itemView.findViewById(R.id.product_photo);
			productId = -1;
		}
	}

//	public static class ViewHolder extends RecyclerView.ViewHolder {
//
//		CardView cv;
//		TextView productName;
//		TextView productBrand;
//		TextView productPrice;
//		ImageView productPhoto;
//
//		public ViewHolder(View v) {
//			super(v);
//			// Define click listener for the ViewHolder's View.
//			v.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Log.d(TAG, "Element " + getPosition() + " clicked.");
//				}
//			});
//			cv = (CardView)itemView.findViewById(R.id.cv);
//			productName = (TextView)itemView.findViewById(R.id.product_name);
//			productBrand = (TextView)itemView.findViewById(R.id.product_brand);
//			productPrice = (TextView)itemView.findViewById(R.id.product_price);
//			productPhoto = (ImageView)itemView.findViewById(R.id.product_photo);
//		}
//	}

	public static class HeaderViewHolder extends ViewHolder {

		private static final String TAG_TITLE = "title";
		private static final String TAG_FILTERS = "filters";
		private static final String TAG_ID = "id";

		Button button;
		int id;
		String filters;
		String type;

		public HeaderViewHolder(View v, final Context context) {
			super(v);
			// Define click listener for the ViewHolder's View.
			button = (Button)itemView.findViewById(R.id.button);
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d("LISTENER", "Element " + getPosition() + " clicked.");
					if (type == null) {
						return;
					}
					Intent intent;
					if (type.equals(FilterButton.CATEGORY)) {
						intent = new Intent(context, SubcategoryActivity.class);
					} else {
						intent = new Intent(context, SubSubcategoryActivity.class);
					}
					intent.putExtra(TAG_TITLE, button.getText());
					intent.putExtra(TAG_ID, id);
					intent.putExtra(TAG_FILTERS, filters);
					context.startActivity(intent);
				}
			});
		}
	}

	private static final String TAG = "CustomAdapter"; /* +++xdebug */
	List<Product> mDataset;
	List<FilterButton> mHeaders;
	Context context;

	// Provide a suitable constructor (depends on the kind of dataset)
	public SubSubcategoryAdapter(List<Product> mDataset, List<FilterButton> mHeaders, Context context) {
		this.mDataset = mDataset;
		this.mHeaders = mHeaders;
		this.context = context;
		Log.d("PRODUCT", "MyAdapter: " + mDataset.size());
	}

	// Create new views (invoked by the layout manager)
	@Override
	public SubSubcategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
	                                               int viewType) {
		// create a new view
		View v;
		ViewHolder viewHolder;
		int listViewItemType = getItemViewType(viewType);
		if (listViewItemType < mHeaders.size()) {
			v = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.search_product_button, parent, false);
			viewHolder = new HeaderViewHolder(v, context);
		} else if (listViewItemType == mHeaders.size() || listViewItemType
				== mHeaders.size() + mDataset.size() + 1) {
			v = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.search_product_space, parent, false);
			viewHolder = new ViewHolder(v);
		} else {
			v = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.card_product, parent, false);
			viewHolder = new ItemViewHolder(v, context);
		}

		return viewHolder;
	}

	@Override
	public int getItemViewType(int position) {
		return position;
	}

	// Replace the contents of a view (invoked by the layout manager)
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		// - get element from your dataset at this position
		// - replace the contents of the view with that element
		int listViewItemType = getItemViewType(position);
		if (listViewItemType < mHeaders.size()) {
			HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
			headerHolder.button.setText(mHeaders.get(position).getName());
			headerHolder.id = mHeaders.get(position).getId();
			headerHolder.filters = mHeaders.get(position).getFilters();
			headerHolder.type = mHeaders.get(position).getType();

		} else if (listViewItemType == mHeaders.size() || listViewItemType
				== mHeaders.size() + mDataset.size() + 1) {

		}
		else {
			ItemViewHolder itemHolder = (ItemViewHolder) holder;
			int newPosition = position - mHeaders.size() - 1;
			itemHolder.productName.setText(mDataset.get(newPosition).getName());
			itemHolder.productBrand.setText(mDataset.get(newPosition).getBrand());
			itemHolder.productPrice.setText(mDataset.get(newPosition).getPrice());
			itemHolder.productPhoto.setImageDrawable(mDataset.get(newPosition).getPhoto());
			itemHolder.productId = mDataset.get(newPosition).getId();
		}


	}

	// Return the size of your dataset (invoked by the layout manager)
	@Override
	public int getItemCount() {
		return mHeaders.size() + 1 + mDataset.size();
	}

//	// Create new views (invoked by the layout manager)
//	@Override
//	public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
//	                                               int viewType) {
//		// create a new view
//		View v = LayoutInflater.from(parent.getContext())
//				.inflate(R.layout.card_product, parent, false);
//
//		return new ViewHolder(v);
//	}
//
//	// Replace the contents of a view (invoked by the layout manager)
//	@Override
//	public void onBindViewHolder(ViewHolder holder, int position) {
//		// - get element from your dataset at this position
//		// - replace the contents of the view with that element
//		holder.productName.setText(mDataset.get(position).getName());
//		holder.productBrand.setText(mDataset.get(position).getBrand());
//		holder.productPrice.setText(mDataset.get(position).getPrice());
//		holder.productPhoto.setImageDrawable(mDataset.get(position).getPhoto());
//
//	}
//
//	// Return the size of your dataset (invoked by the layout manager)
//	@Override
//	public int getItemCount() {
//		return mDataset.size();
//	}

}
