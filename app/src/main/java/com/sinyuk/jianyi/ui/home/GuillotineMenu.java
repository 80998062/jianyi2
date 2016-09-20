package com.sinyuk.jianyi.ui.home;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sinyuk.jianyi.App;
import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.api.AccountManger;
import com.sinyuk.jianyi.api.oauth.OauthModule;
import com.sinyuk.jianyi.data.player.Player;
import com.sinyuk.jianyi.data.school.School;
import com.sinyuk.jianyi.ui.BaseFragment;
import com.sinyuk.jianyi.ui.events.LoginEvent;
import com.sinyuk.jianyi.ui.events.LogoutEvent;
import com.sinyuk.jianyi.ui.login.JianyiLoginActivity;
import com.sinyuk.jianyi.ui.player.PlayerActivity;
import com.sinyuk.jianyi.utils.TextViewHelper;
import com.sinyuk.jianyi.utils.ToastUtils;
import com.sinyuk.jianyi.utils.glide.BlurTransformation;
import com.sinyuk.jianyi.utils.glide.CropCircleTransformation;
import com.sinyuk.jianyi.widgets.MyCircleImageView;
import com.sinyuk.jianyi.widgets.RatioImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import dagger.Lazy;

/**
 * Created by Sinyuk on 16/9/11.
 */
public class GuillotineMenu extends BaseFragment {
    private static final int BLUR_RADIUS = 28;
    private static final int BLUR_SAMPLING = 14;
    private static final String TAG = "GuillotineMenu";

    @Inject
    Lazy<AccountManger> accountMangerLazy;
    @BindView(R.id.background)
    RatioImageView mBackground;
    @BindView(R.id.avatar)
    MyCircleImageView mAvatar;
    @BindView(R.id.user_name_tv)
    EditText mUserNameTv;
    @BindView(R.id.school_tv)
    EditText mSchoolTv;
    @BindView(R.id.login_hint_tv)
    TextView mLoginHint;
    @BindView(R.id.inbox_btn)
    Button mInboxBtn;
    @BindView(R.id.profile_btn)
    Button mProfileBtn;
    @BindView(R.id.logout_btn)
    Button mLogoutBtn;
    @BindView(R.id.container)
    LinearLayout mContainer;
    @Inject
    Lazy<ToastUtils> toastUtilsLazy;
    private Player mPlayer;
    private boolean isLoggedIn = false;

    @Override
    protected void beforeInflate() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getRootViewId() {
        return R.layout.fragment_guillotine_menu;
    }

    @Override
    protected void finishInflate() {
        setupLayoutTransition();

        isLoggedIn = accountMangerLazy.get().isLoggedIn();

        if (isLoggedIn) {
            addSubscription(accountMangerLazy.get()
                    .getCurrentUser()
                    .subscribe(player -> {
                        mPlayer = player;
                    }));
        }

        updateUI();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        App.get(context).getAppComponent().plus(new OauthModule()).inject(this);
    }

    private void setupLayoutTransition() {
        LayoutTransition transition = new LayoutTransition();

        ObjectAnimator addAnimator = ObjectAnimator.ofFloat(
                null, "alpha", 0, 1).setDuration(2000);
        transition.setAnimator(LayoutTransition.APPEARING, addAnimator);

        ObjectAnimator removeAnimator = ObjectAnimator.ofFloat(
                null, "alpha", 1, 0).setDuration(2000);
        transition.setAnimator(LayoutTransition.DISAPPEARING, removeAnimator);

        transition.setStagger(LayoutTransition.APPEARING, 1000);
        transition.setStagger(LayoutTransition.DISAPPEARING, 1000);

        mContainer.setLayoutTransition(transition);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoggedIn(LoginEvent event) {
        Log.d(TAG, "onLoggedIn: ");
        isLoggedIn = true;
        mPlayer = event.getPlayer();
        updateUI();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogout(LogoutEvent event) {
        Log.d(TAG, "onLogout: ");
        isLoggedIn = false;
        mPlayer = null;
        updateUI();
    }

    private void updateUI() {

        toggleVisibility(isLoggedIn);

        if (isLoggedIn && mPlayer != null) {
            TextViewHelper.setText(mUserNameTv, mPlayer.getName(), mPlayer.getId() + "");

            TextViewHelper.setText(mSchoolTv, mPlayer.getSchoolName(), null);

            final int errorPlaceholder = mPlayer.getSex() == 0 ? R.drawable.boy : R.drawable.girl;

            Glide.with(this).load(mPlayer.getAvatar())
                    .bitmapTransform(new CropCircleTransformation(getContext()))
                    .error(errorPlaceholder)
                    .into(mAvatar);

            Log.d(TAG, "avatar: " + mPlayer.getAvatar());

            Glide.with(this).load(mPlayer.getAvatar())
                    .crossFade(2000)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .bitmapTransform(new BlurTransformation(getContext(), BLUR_RADIUS, BLUR_SAMPLING))
                    .into(mBackground);
        } else {
            Log.d(TAG, "not logged in: ");
            Glide.with(this).load(R.drawable.boy)
                    .bitmapTransform(new CropCircleTransformation(getContext()))
                    .into(mAvatar);

            mBackground.setImageDrawable(null);
        }
    }

    private void toggleVisibility(boolean isLoggedIn) {
        int visibility = isLoggedIn ? View.VISIBLE : View.GONE;
        int reverse = isLoggedIn ? View.GONE : View.VISIBLE;
        mUserNameTv.setVisibility(visibility);
        mSchoolTv.setVisibility(visibility);
        mLoginHint.setVisibility(reverse);

        mInboxBtn.setVisibility(visibility);
        mProfileBtn.setVisibility(visibility);
        mLogoutBtn.setVisibility(visibility);
    }

    @OnClick(R.id.profile_btn)
    public void goToProfile() {
        Pair<View, String> p1 = Pair.create((View) mAvatar, getString(R.string.transition_avatar));
        Pair<View, String> p2 = Pair.create(mSchoolTv, getString(R.string.transition_school));
        Pair<View, String> p3 = Pair.create((View) mUserNameTv, getString(R.string.transition_username));
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation((Activity) getContext(), p1, p2, p3);

        final School school = new School(mPlayer.getSchool(), mPlayer.getSchoolName());

        Intent starter = new Intent(getContext(), PlayerActivity.class);
        starter.putExtra(PlayerActivity.KEY_PLAYER, mPlayer);
        starter.putExtra(PlayerActivity.KEY_SCHOOL, school);
        startActivity(starter/*, options.toBundle()*/);
    }

    @OnClick(R.id.login_hint_tv)
    public void onClickAvatar(View view) {
        final Rect rect = new Rect();
        view.getDrawingRect(rect);
        final ViewGroup parent = (ViewGroup) view.getParent();
        parent.offsetDescendantRectToMyCoords(view, rect);
        int centerX = rect.centerX();
        int centerY = rect.centerY();
        int radius = Math.min(rect.width(), rect.height());
        rect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        JianyiLoginActivity.start(getContext(), rect);
    }

    @OnClick({R.id.inbox_btn, R.id.profile_btn, R.id.logout_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.inbox_btn:
                Log.d(TAG, "onClick: " + "inbox");
                break;
            case R.id.profile_btn:
                break;
            case R.id.logout_btn:
                Log.d(TAG, "onClick: " + "logout");
                accountMangerLazy.get().logout();
                toastUtilsLazy.get().toastShort("退出登录");
                break;
        }
    }
}
