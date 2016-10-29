package ar.edu.itba.hci.q2.g4.androidapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class PhotosProductDetailAdapter extends RecyclerView.Adapter<PhotosProductDetailAdapter.ViewHolder> {
	public static class ViewHolder extends RecyclerView.ViewHolder {
		ImageView image;

		final static String TAG = "ProductDetailAdapter"; /* +++xdebug */

		public ViewHolder(View v, Context context) {
			super(v);
			// Define click listener for the ViewHolder's View.
			v.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, "Element " + getPosition() + " " +
							"clicked.");
					/* +++xtodo: usar el context aca para lanzar intents */
				}
			});
			image = (ImageView)itemView.findViewById(R.id.product_photo);
		}
	}

	private List<Drawable> mDataSet;

	/* needed to call an intent when the on click is executed */
	private Context context;

	public PhotosProductDetailAdapter(List<Drawable> mDataSet, Context context) {
		this.mDataSet = mDataSet;
		this.context = context;
	}

	// Create new views (invoked by the layout manager)
	@Override
	public PhotosProductDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
	                                                     int viewType) {
		// create a new view
		View v = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.card_product_detail, parent, false);

		return new ViewHolder(v, context);
	}

	@Override
	public int getItemViewType(int position) {
		return position;
	}

	// Replace the contents of a view (invoked by the layout manager)
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.image.setImageDrawable(mDataSet.get(position));
	}

	// Return the size of your dataset (invoked by the layout manager)
	@Override
	public int getItemCount() {
		return mDataSet.size();
	}
}
