package me.hekr.sthome.CarouselView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lib.funsdk.support.FunPath;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;

import me.hekr.sthome.R;
import me.hekr.sthome.commonBaseView.FuncSDKImage;
import me.hekr.sthome.commonBaseView.Weatherlinearlayout;
import me.hekr.sthome.model.modelbean.WeatherInfoBean;

/**
 * ImageView创建工厂
 */
public class ViewFactory {

	/**
	 * 获取ImageView视图的同时加载显示url
	 *
	 * @return
	 */
	public static FrameLayout getImageView(Context context, String devid) {
		FuncSDKImage imageView = (FuncSDKImage)LayoutInflater.from(context).inflate(
				R.layout.view_banner, null);
		imageView.setImageResource(R.drawable.u2);
		imageView.setVisibility(View.VISIBLE);
		String path = FunPath.getAutoCapturePath(devid);
		File file = new File(path);
		try {
			if(file.exists()){
				Bitmap mm = BitmapFactory.decodeFile(path);
				imageView.setImageBitmap(mm);
			}else{
				imageView.setImageResourceNoMark(R.drawable.u3);
			}
		}catch (Exception e){
			imageView.setImageResourceNoMark(R.drawable.u3);
		}

		return imageView;
	}

	public static FrameLayout getImageView2(Context context) {
		FuncSDKImage imageView = (FuncSDKImage)LayoutInflater.from(context).inflate(
				R.layout.view_banner, null);
		imageView.setImageResourceNoMark(R.drawable.u3);
		imageView.setVisibility(View.VISIBLE);
		return imageView;
	}

	public static FrameLayout getDeviceListView(Context context) {
		FrameLayout layout = (FrameLayout) LayoutInflater.from(context).inflate(
				R.layout.view_device_list, null);
		return layout;
	}


	public static LinearLayout getweatherLinearLayout(Context context, WeatherInfoBean equipmentBean) {
		Weatherlinearlayout imageView = null;


		if(equipmentBean.isFlag_first()){
			imageView = (Weatherlinearlayout)LayoutInflater.from(context).inflate(
					R.layout.view_banner_weather, null);
			TextView txt_city = (TextView)imageView.findViewById(R.id.gps);
			TextView txt_humidy = (TextView)imageView.findViewById(R.id.HShow);
			TextView txt_temp   = (TextView)imageView.findViewById(R.id.TShow);
			final ImageView imageView1  = (ImageView)imageView.findViewById(R.id.weather);
			TextView txt_weather = (TextView)imageView.findViewById(R.id.weater_txt);
			txt_city.setText(equipmentBean.getName());
			txt_humidy.setText(equipmentBean.getHum());
			txt_temp.setText(equipmentBean.getTemp());
			txt_weather.setText(equipmentBean.getWeather());

             if(!TextUtils.isEmpty(equipmentBean.getWeather_ico_url()))
            ImageLoader.getInstance().loadImage("https://openweathermap.org/themes/openweathermap/assets/vendor/owm/img/widgets/" + equipmentBean.getWeather_ico_url() + ".png",new SimpleImageLoadingListener() {

                                        @Override
                                        public void onLoadingComplete(String imageUri, View view,
                                                                      Bitmap loadedImage) {
                                            super.onLoadingComplete(imageUri, view, loadedImage);
											imageView1.setImageBitmap(loadedImage);
                                        }

                                    });
		}else{
			imageView = (Weatherlinearlayout)LayoutInflater.from(context).inflate(
					R.layout.view_banner_th, null);
			TextView txt_city = (TextView)imageView.findViewById(R.id.gps);
			TextView txt_humidy = (TextView)imageView.findViewById(R.id.HShow);
			TextView txt_temp   = (TextView)imageView.findViewById(R.id.TShow);
			txt_city.setText(equipmentBean.getName());
			txt_humidy.setText(equipmentBean.getHum());
			txt_temp.setText(equipmentBean.getTemp());
		}

		return imageView;
	}

}
