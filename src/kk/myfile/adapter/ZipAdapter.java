package kk.myfile.adapter;

import java.util.ArrayList;
import java.util.List;

import kk.myfile.R;
import kk.myfile.activity.DirectActivity.Node;
import kk.myfile.activity.BaseActivity.Classify;
import kk.myfile.activity.ZipActivity;
import kk.myfile.file.Sorter;
import kk.myfile.leaf.Direct;
import kk.myfile.leaf.Leaf;
import kk.myfile.util.AppUtil;
import kk.myfile.util.DataUtil;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ZipAdapter extends BaseAdapter {
	private final ZipActivity mActivity;
	private final List<Direct> mData = new ArrayList<Direct>();
	private Object mMark;

	public ZipAdapter(ZipActivity activity) {
		mActivity = activity;
	}

	public void setData(final List<Leaf> data, final int position) {
		mMark = data;

		AppUtil.runOnNewThread(new Runnable() {
			@Override
			public void run() {
				synchronized (data) {
					Sorter.sort(Classify.Direct, data);
				}

				AppUtil.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (mMark == data) {
							mData.clear();
							synchronized (data) {
								for (Leaf leaf : data) {
									if (leaf instanceof Direct) {
										mData.add((Direct) leaf);
									}
								}
							}

							notifyDataSetChanged();

							AppUtil.runOnUiThread(new Runnable() {
								public void run() {
									mActivity.setSelection(position);
								}
							});
						}
					}
				});
			}
		});
	}

	@Override
	public int getCount() {
		return mData == null ? 0 : mData.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final ViewHolder holder;

		if (view == null) {
			view = mActivity.getLayoutInflater().inflate(R.layout.grid_select, null);

			holder = new ViewHolder();
			holder.icon = (ImageView) view.findViewById(R.id.iv_icon);
			holder.name = (TextView) view.findViewById(R.id.tv_name);
			view.setTag(holder);

			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					mActivity.showDirect(new Node(holder.data), true);
				}
			});
		} else {
			holder = (ViewHolder) view.getTag();
		}

		Direct data = mData.get(position);

		holder.icon.setImageResource(data.getIcon());
		holder.name.setText(DataUtil.getName(data.getPath()));
		holder.data = data;

		return view;
	}

	class ViewHolder {
		public ImageView icon;
		public TextView name;
		public Direct data;
	}
}