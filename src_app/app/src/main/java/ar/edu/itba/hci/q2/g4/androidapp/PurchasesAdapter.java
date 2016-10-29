package ar.edu.itba.hci.q2.g4.androidapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.List;

public class PurchasesAdapter extends RecyclerView.Adapter<PurchasesAdapter
.ViewHolder> {
// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
public static class ViewHolder extends RecyclerView.ViewHolder {
	CardView cv;
	ImageView orderState;
	TextView orderId;
	TextView orderDate;
	TextView orderPrice;

	private String TAG = "PurchasesAdapter";
	public ViewHolder(View v, final Context context) {
		super(v);

		cv = (CardView)itemView.findViewById(R.id.cv);
		orderState = (ImageView)itemView.findViewById(R.id.order_status);
		orderId = (TextView)itemView.findViewById(R.id.order_id);
		orderDate = (TextView)itemView.findViewById(R.id.order_creation_date);
		orderPrice = (TextView)itemView.findViewById(R.id.order_price);

		// Define click listener for the ViewHolder's View.
		v.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Element " + getPosition() + " " +
						"clicked.");
				Intent intent = new Intent(context,
						PurchaseDetailActivity.class);
				intent.putExtra("ORDER_ID", orderId.getText());
				context.startActivity(intent);
			}
		});
	}
}

private static final String TAG = "CustomAdapter"; /* +++xdebug */
	List<Purchase> mDataset;

	private int CONFIRMED = 0xff607D8B;
	private int TRANSPORTED = 0xffFFC107;
	private int DELIVERED = 0xff4CAF50;

	private Context context;
	private TextDrawable.IBuilder mDrawableBuilder;
	// Provide a suitable constructor (depends on the kind of dataset)
	public PurchasesAdapter(List<Purchase> mDataset, Context context) {
		this.mDataset = mDataset;
		this.context = context;
		mDrawableBuilder = TextDrawable.builder()
				.round();
	}

	// Create new views (invoked by the layout manager)
	@Override
	public PurchasesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
	                                               int viewType) {
		// create a new view
		View v;
		ViewHolder viewHolder;
		v = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.card_purchases, parent, false);
		viewHolder = new ViewHolder(v, context);
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

		switch (mDataset.get(position).getState()) {
			case CONFIRMED:
				holder.orderState.setImageDrawable(mDrawableBuilder.build(
						mDataset.get(position).getStateString(), CONFIRMED));
				break;
			case TRANSPORTED:
				holder.orderState.setImageDrawable(mDrawableBuilder.build(
						mDataset.get(position).getStateString(), TRANSPORTED));
				break;
			case DELIVERED:
				holder.orderState.setImageDrawable(mDrawableBuilder.build(
						mDataset.get(position).getStateString(), DELIVERED));
				break;
		}

		holder.orderId.setText(mDataset.get(position).getId());
		holder.orderDate.setText(mDataset.get(position).getConfirmedDate());
		holder.orderPrice.setText(mDataset.get(position).getPrice());
	}

	// Return the size of your dataset (invoked by the layout manager)
	@Override
	public int getItemCount() {
		return mDataset.size();//+++xnullpointerexception
	}
}
