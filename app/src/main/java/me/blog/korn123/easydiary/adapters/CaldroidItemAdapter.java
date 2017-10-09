package me.blog.korn123.easydiary.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

import java.util.List;
import java.util.Map;

import hirondelle.date4j.DateTime;
import me.blog.korn123.commons.utils.EasyDiaryUtils;
import me.blog.korn123.easydiary.R;
import me.blog.korn123.easydiary.helper.EasyDiaryDbHelper;
import me.blog.korn123.easydiary.models.DiaryDto;

public class CaldroidItemAdapter extends CaldroidGridAdapter {

	public CaldroidItemAdapter(Context context, int month, int year,
                               Map<String, Object> caldroidData,
                               Map<String, Object> extraData) {
		super(context, month, year, caldroidData, extraData);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View cellView = convertView;

		// For reuse
		if (convertView == null) {
			cellView = inflater.inflate(R.layout.fragment_custom_cell, null);
		}

		int topPadding = cellView.getPaddingTop();
		int leftPadding = cellView.getPaddingLeft();
		int bottomPadding = cellView.getPaddingBottom();
		int rightPadding = cellView.getPaddingRight();

		TextView tv1 = (TextView) cellView.findViewById(R.id.tv1);
		TextView tv2 = (TextView) cellView.findViewById(R.id.tv2);
		ImageView imageView1 = (ImageView) cellView.findViewById(R.id.weather);

		tv1.setTextColor(Color.BLACK);

		// Get dateTime of this cell
		DateTime dateTime = this.datetimeList.get(position);
		Resources resources = context.getResources();

		// Set color of the dates in previous / next month
		if (dateTime.getMonth() != month) {
			tv1.setTextColor(resources
					.getColor(com.caldroid.R.color.caldroid_darker_gray));
		}

		boolean shouldResetDiabledView = false;
		boolean shouldResetSelectedView = false;

		// Customize for disabled dates and date outside min/max dates
		if ((minDateTime != null && dateTime.lt(minDateTime))
				|| (maxDateTime != null && dateTime.gt(maxDateTime))
				|| (disableDates != null && disableDates.indexOf(dateTime) != -1)) {

			tv1.setTextColor(CaldroidFragment.disabledTextColor);
			if (CaldroidFragment.disabledBackgroundDrawable == -1) {
				cellView.setBackgroundResource(com.caldroid.R.drawable.disable_cell);
			} else {
				cellView.setBackgroundResource(CaldroidFragment.disabledBackgroundDrawable);
			}

			if (dateTime.equals(getToday())) {
				cellView.setBackgroundResource(com.caldroid.R.drawable.red_border_gray_bg);
			}

		} else {
			shouldResetDiabledView = true;
		}

		// Customize for selected dates
		if (selectedDates != null && selectedDates.indexOf(dateTime) != -1) {
			cellView.setBackgroundResource(R.drawable.bg_card_cell_select_selector);

			tv1.setTextColor(Color.BLACK);

		} else {
			shouldResetSelectedView = true;
		}

		if (shouldResetDiabledView && shouldResetSelectedView) {
			// Customize for today
			if (dateTime.equals(getToday())) {
				cellView.setBackgroundResource(R.drawable.bg_card_cell_today_selector);
			} else {
				cellView.setBackgroundResource(R.drawable.bg_card_cell_default);
			}
		}

		tv1.setText("" + dateTime.getDay());

		String dateString = dateTime.format("YYYY-MM-DD");
		int count = EasyDiaryDbHelper.countDiaryBy(dateString);

		List<DiaryDto> mDiaryList = EasyDiaryDbHelper.readDiaryByDateString(dateString);
		boolean initWeather = false;
		if (mDiaryList.size() > 0) {
			for (DiaryDto diaryDto : mDiaryList) {
				if (diaryDto.getWeather() > 0) {
					initWeather = true;
					EasyDiaryUtils.initWeatherView(imageView1, diaryDto.getWeather());
					break;
				}
			}
			if (!initWeather) {
				imageView1.setVisibility(View.GONE);
				imageView1.setImageResource(0);
			}
		} else {
			imageView1.setVisibility(View.GONE);
			imageView1.setImageResource(0);
		}

		if (count > 0) {
			tv2.setText(count + parent.getResources().getString(R.string.diary_count));
			tv2.setTextColor(parent.getResources().getColor(R.color.diaryCountText));
		} else {
			tv2.setText(null);
		}
		// Somehow after setBackgroundResource, the padding collapse.
		// This is to recover the padding
		cellView.setPadding(leftPadding, topPadding, rightPadding,
				bottomPadding);

		// Set custom color if required
		setCustomResources(dateTime, cellView, tv1);

		return cellView;
	}

}
