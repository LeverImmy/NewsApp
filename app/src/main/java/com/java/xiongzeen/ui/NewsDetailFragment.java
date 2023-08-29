package com.java.xiongzeen.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.java.xiongzeen.MyApplication;
import com.java.xiongzeen.R;
import com.java.xiongzeen.data.News;
import com.java.xiongzeen.service.NewsManager;
import com.java.xiongzeen.service.PictureLoader;


public class NewsDetailFragment extends Fragment {
    private Context context;
    private ImageView news_image, news_image2, news_image3, news_image4;
    private Long news_id = -1L;
    private FloatingActionButton button1, button2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reading_news_one_pic, container, false);
        if (getArguments() != null) {
            news_id = getArguments().getLong("newsId",-1);
        }
        News newsToShow;
        if (news_id >= 0) {
            newsToShow = NewsManager.getInstance().getNews(news_id);
        } else {
            newsToShow = new News();
        }

        context = view.getContext();

        TextView news_title = view.findViewById(R.id.detail_title);
        TextView news_description = view.findViewById(R.id.detail_description);
        TextView news_content = view.findViewById(R.id.detail_content);
        news_image = view.findViewById(R.id.detail_image1);
        news_image2 = view.findViewById(R.id.detail_image2);
        news_image3 = view.findViewById(R.id.detail_image3);
        news_image4 = view.findViewById(R.id.detail_image4);
        FragmentContainerView containerView = view.findViewById(R.id.fragment_to_contain_video);

        button1 = view.findViewById(R.id.favoriteFloatingActionButton);
        button2 = view.findViewById(R.id.favoriteFloatingActionButton2);
        Log.d("NewsDetailFragment","open_a_news" + newsToShow.getId());
        news_title.setText(newsToShow.getTitle());
        news_description.setText(newsToShow.getPublisher() + "     " + newsToShow.getPublishTime());
        news_content.setText(newsToShow.getContent());

        button1.setOnClickListener(v->{handle_favorite_click(1);});
        button2.setOnClickListener(v->{handle_favorite_click(2);});

        if (!newsToShow.isFavorites()) {
            button2.setVisibility(View.GONE);
        } else {
            button1.setVisibility(View.GONE);
        }

        if (newsToShow.getImages().length >= 1) {
            PictureLoader.loadPictureWithoutPlaceHolder(context, newsToShow.getImages()[0],news_image);
        } else {
            news_image.setVisibility(View.GONE);
        }
        if (newsToShow.getImages().length >= 2) {
            PictureLoader.loadPictureWithoutPlaceHolder(context, newsToShow.getImages()[1],news_image2);
        } else {
            news_image2.setVisibility(View.GONE);
        }
        if (newsToShow.getImages().length >= 3) {
            PictureLoader.loadPictureWithoutPlaceHolder(context, newsToShow.getImages()[2],news_image3);
        } else {
            news_image3.setVisibility(View.GONE);
        }
        if (newsToShow.getImages().length >= 4) {
            PictureLoader.loadPictureWithoutPlaceHolder(context, newsToShow.getImages()[3],news_image4);
        } else {
            news_image4.setVisibility(View.GONE);
        }
        if (newsToShow.getVideo() != null && newsToShow.getVideo().length() != 0) {
            String path = newsToShow.getVideo();
            VideoFragment to_fill = VideoFragment.newInstance(path);
            getParentFragmentManager().beginTransaction().add(R.id.fragment_to_contain_video, to_fill).commit();
        } else {
            containerView.setVisibility(View.GONE);
        }

        MyApplication.getBottomNavigationView().setVisibility(View.GONE);
        MyApplication.getTopFragmentContainer().setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        MyApplication.getBottomNavigationView().setVisibility(View.VISIBLE);
        if(MyApplication.newsPage && !MyApplication.newsPageIsSearchingPage)
            MyApplication.getTopFragmentContainer().setVisibility(View.VISIBLE);
    }

    private void handle_favorite_click(int i) {
        Toast.makeText(context,""+ i, Toast.LENGTH_SHORT);
        if(i == 1) {
            NewsManager.getInstance().favorite_triggered(news_id,true);
            button2.setVisibility(View.VISIBLE);
            button1.setVisibility(View.GONE);
        } else {
            NewsManager.getInstance().favorite_triggered(news_id,false);
            button1.setVisibility(View.VISIBLE);
            button2.setVisibility(View.GONE);
        }
    }
}