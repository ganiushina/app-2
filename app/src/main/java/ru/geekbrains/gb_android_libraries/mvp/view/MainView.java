package ru.geekbrains.gb_android_libraries.mvp.view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

@StateStrategyType(value = AddToEndSingleStrategy.class)
public interface MainView extends MvpView {
    void init();
    void updateList();
    @StateStrategyType(OneExecutionStateStrategy.class)
    void showMessage(String text);

    void showLoading();
    void hideLoading();

    void setUsername(String login);
    void loadImage(String avatarUrl);
}
