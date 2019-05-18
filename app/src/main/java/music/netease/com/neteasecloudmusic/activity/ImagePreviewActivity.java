package music.netease.com.neteasecloudmusic.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.tools.widget.CustomViewPager;

import java.util.List;
import java.util.Map;

import music.netease.com.neteasecloudmusic.R;
import music.netease.com.neteasecloudmusic.adapter.ImagePreviewAdapter;
import music.netease.com.neteasecloudmusic.constant.ImageCanstant;


public class ImagePreviewActivity extends BaseActivity {

    private int itemPosition;
    private List<String> imageList;
    private CustomViewPager viewPager;
    private LinearLayout main_linear;
    private boolean mIsReturning;
    private int mStartPosition;
    private int mCurrentPosition;
    private ImagePreviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        initShareElement();
        getIntentData();
        initView();
        renderView();
        getData();
        setListener();
    }

    private void initShareElement() {
        setEnterSharedElementCallback(mCallback);
    }

    private void setListener() {
        main_linear.getChildAt(mCurrentPosition).setEnabled(true);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                hideAllIndicator(position);
                main_linear.getChildAt(position).setEnabled(true);
                mCurrentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                final float normalizedposition = Math.abs(Math.abs(position) - 1);
                page.setScaleX(normalizedposition / 2 + 0.5f);
                page.setScaleY(normalizedposition / 2 + 0.5f);
            }
        });
    }

    private void hideAllIndicator(int position) {
        for (int i = 0; i < imageList.size(); i++) {
            if (i != position) {
                main_linear.getChildAt(i).setEnabled(false);
            }
        }
    }

    private void initView() {
        viewPager = findViewById(R.id.imageBrowseViewPager);
        main_linear = findViewById(R.id.main_linear);
    }

    private void renderView() {
        if (imageList == null)
            return;
        if (imageList.size() == 1) {
            main_linear.setVisibility(View.GONE);
        } else {
            main_linear.setVisibility(View.VISIBLE);
        }
        adapter = new ImagePreviewAdapter(this, imageList, itemPosition);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(mCurrentPosition);
    }

    private void getIntentData() {
        if (getIntent() != null) {
            mStartPosition = getIntent().getIntExtra(ImageCanstant.START_IAMGE_POSITION, 0);
            mCurrentPosition = mStartPosition;
            itemPosition = getIntent().getIntExtra(ImageCanstant.START_ITEM_POSITION, 0);
            imageList = getIntent().getStringArrayListExtra("imageList");
        }
    }

    /**
     * 获取数据
     */
    private void getData() {

        View view;
        for (String pic : imageList) {

            //创建底部指示器(小圆点)
            view = new View(ImagePreviewActivity.this);
            view.setBackgroundResource(R.drawable.indicator);
            view.setEnabled(false);
            //设置宽高
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(20, 20);
            //设置间隔
            if (!pic.equals(imageList.get(0))) {
                layoutParams.leftMargin = 20;
            }
            //添加到LinearLayout
            main_linear.addView(view, layoutParams);
        }
    }


    @Override
    public void finishAfterTransition() {
        mIsReturning = true;
        Intent data = new Intent();
        data.putExtra(ImageCanstant.START_IAMGE_POSITION, mStartPosition);
        data.putExtra(ImageCanstant.CURRENT_IAMGE_POSITION, mCurrentPosition);
        data.putExtra(ImageCanstant.CURRENT_ITEM_POSITION, itemPosition);
        setResult(RESULT_OK, data);
        super.finishAfterTransition();
    }


    private final SharedElementCallback mCallback = new SharedElementCallback() {

        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            if (mIsReturning) {
                ImageView sharedElement = adapter.getPhotoView();
                if (sharedElement == null) {
                    names.clear();
                    sharedElements.clear();
                } else if (mStartPosition != mCurrentPosition) {
                    names.clear();
                    names.add(sharedElement.getTransitionName());
                    sharedElements.clear();
                    sharedElements.put(sharedElement.getTransitionName(), sharedElement);
                }
            }
        }
    };
}