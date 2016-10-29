package ar.edu.itba.hci.q2.g4.androidapp;

import android.content.Context;
import android.os.AsyncTask;

public abstract class AsyncTaskWithContext<S, T, U> extends AsyncTask<S, T, U> {

	private Context context;

	/**
	 *  Should be call always before execute
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}


}
