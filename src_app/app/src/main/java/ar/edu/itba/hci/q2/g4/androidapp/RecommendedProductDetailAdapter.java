package ar.edu.itba.hci.q2.g4.androidapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class RecommendedProductDetailAdapter extends RecyclerView.Adapter<RecommendedProductDetailAdapter.ViewHolder> {
	public static class ViewHolder extends RecyclerView.ViewHolder {
		ImageView image;
		int productId;
		String productName;

		private static final String TAG_ID = "id";
		private static final String TAG_TITLE = "title";

		final static String TAG = "ProductDetailAdapter"; /* +++xdebug */

		public ViewHolder(View v, final Context context) {
			super(v);
			// Define click listener for the ViewHolder's View.
			v.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, ProductDetailActivity.class);
					intent.putExtra(TAG_TITLE, productName);
					intent.putExtra(TAG_ID, productId);
					context.startActivity(intent);
				}
			});
			image = (ImageView)itemView.findViewById(R.id.product_photo);
		}
	}

	private List<Product> mDataSet;

	/* needed to call an intent when the on click is executed */
	private Context context;

	public RecommendedProductDetailAdapter(List<Product> mDataSet, Context context) {
		this.mDataSet = mDataSet;
		this.context = context;
	}

	// Create new views (invoked by the layout manager)
	@Override
	public RecommendedProductDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
	                                                                int viewType) {
		// create a new view
		View v = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.card_product_detail_recommended, parent, false);

		return new ViewHolder(v, context);
	}

	@Override
	public int getItemViewType(int position) {
		return position;
	}

	// Replace the contents of a view (invoked by the layout manager)
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
//		holder.image.setImageResource(mDataSet.get(position).getPhotoId());
		holder.image.setImageDrawable(mDataSet.get(position).getPhoto());
		holder.productId = mDataSet.get(position).getId();
		holder.productName = mDataSet.get(position).getName();
	}

	// Return the size of your dataset (invoked by the layout manager)
	@Override
	public int getItemCount() {
		return mDataSet.size();
	}
}
