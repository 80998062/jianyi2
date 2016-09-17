package com.sinyuk.jianyi.ui.player;

import android.content.Context;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.sinyuk.jianyi.App;
import com.sinyuk.jianyi.R;
import com.sinyuk.jianyi.api.AccountManger;
import com.sinyuk.jianyi.data.goods.Goods;
import com.sinyuk.jianyi.data.player.PlayerRepository;
import com.sinyuk.jianyi.data.player.PlayerRepositoryModule;
import com.sinyuk.jianyi.ui.BaseFragment;
import com.sinyuk.jianyi.ui.detail.DetailActivity;
import com.sinyuk.jianyi.ui.goods.GoodsItemDecoration;
import com.sinyuk.jianyi.utils.BetterViewAnimator;
import com.sinyuk.jianyi.utils.FormatUtils;
import com.sinyuk.jianyi.utils.FuzzyDateFormater;
import com.sinyuk.jianyi.utils.PrefsKeySet;
import com.sinyuk.jianyi.utils.TextViewHelper;
import com.sinyuk.jianyi.utils.list.SlideInUpAnimator;
import com.sinyuk.jianyi.widgets.LabelView;
import com.sinyuk.jianyi.widgets.RatioImageView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.Lazy;
import rx.Observer;

/**
 * Created by Sinyuk on 16/9/14.
 */
public class ManagerSheetFragment extends BaseFragment {
    public static final String KEY_ID = "ID";
    private static final int PRELOAD_THRESHOLD = 2;
    private static final int FIRST_PAGE = 1;
    @BindView(R.id.layout_loading)
    FrameLayout mLayoutLoading;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.layout_empty)
    FrameLayout mLayoutEmpty;
    @BindView(R.id.view_animator)
    BetterViewAnimator mViewAnimator;
    @Inject
    Lazy<AccountManger> accountMangerLazy;
    @Inject
    PlayerRepository playerRepository;
    @Inject
    RxSharedPreferences preferences;
    private int id = 0;
    private boolean isLoading;
    private ManagerAdapter mAdapter;

    private int page = FIRST_PAGE;
    private final Observer<List<Goods>> refreshObserver = new Observer<List<Goods>>() {
        @Override
        public void onCompleted() {
            page = FIRST_PAGE + 1;
            // 这里还是空就过分了
            mViewAnimator.setDisplayedChildId(mAdapter.getItemCount() == 0 ? R.id.layout_empty : R.id.recycler_view);
        }

        @Override
        public void onError(Throwable e) {
            handleError(e);
        }

        @Override
        public void onNext(List<Goods> items) {
            mAdapter.resetAll(items);
        }
    };

    private final Observer<List<Goods>> loadObserver = new Observer<List<Goods>>() {
        @Override
        public void onCompleted() {
            page++;
        }

        @Override
        public void onError(Throwable e) {
            handleError(e);
        }

        @Override
        public void onNext(List<Goods> items) {
            mAdapter.appendAll(items);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        App.get(context).getAppComponent().plus(new PlayerRepositoryModule()).inject(this);
    }

    @Override
    protected void beforeInflate() {
        id = preferences.getInteger(PrefsKeySet.KEY_USER_ID).get();
    }

    @Override
    protected int getRootViewId() {
        return R.layout.fragment_manager_sheet;
    }

    @Override
    protected void finishInflate() {
        initRecyclerView();
        initData();
    }

    private void initRecyclerView() {
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, OrientationHelper.VERTICAL, false);

        mRecyclerView.setLayoutManager(gridLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setScrollingTouchSlop(RecyclerView.TOUCH_SLOP_PAGING);

        mRecyclerView.setItemAnimator(new SlideInUpAnimator(new FastOutSlowInInterpolator()));

        mRecyclerView.addItemDecoration(new GoodsItemDecoration(getContext()));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (isLoading) {
                    return;
                }
                final GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                boolean isBottom =
                        gridLayoutManager.findLastCompletelyVisibleItemPosition() >= recyclerView.getAdapter().getItemCount() - PRELOAD_THRESHOLD;
                if (isBottom) {
                    loadMore();
                }
            }
        });
    }

    private void initData() {
        mAdapter = new ManagerAdapter();
        mAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mAdapter);
    }


    private void loadMore() {
        addSubscription(playerRepository.getHisPosts(id, page).subscribe(loadObserver));
    }

    private void refresh() {
        addSubscription(playerRepository.getHisPosts(id, 1).subscribe(refreshObserver));
    }

    /**
     * 加载错误时
     *
     * @param throwable
     */
    private void handleError(Throwable throwable) {
        throwable.printStackTrace();
        mViewAnimator.setDisplayedChildId(R.id.layout_empty);
    }


    private class ManagerAdapter extends RecyclerView.Adapter<ManagerAdapter.ManagerItemHolder> {

        private final static int CROSS_FADE_DURATION = 1500;
        private final DrawableRequestBuilder<String> shotBuilder;

        private List<Goods> mDataSet = new ArrayList<>();

        public ManagerAdapter() {
            shotBuilder = Glide.with(ManagerSheetFragment.this).fromString().diskCacheStrategy(DiskCacheStrategy.RESULT).crossFade(CROSS_FADE_DURATION).centerCrop();
        }

        @Override
        public ManagerItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ManagerItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.manager_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ManagerItemHolder holder, int position) {
            if (mDataSet.get(position) == null) return;
            final Goods data = mDataSet.get(position);
            if (TextUtils.isEmpty(data.getPrice())) {
                holder.mPriceLabelView.setVisibility(View.INVISIBLE);
            } else {
                holder.mPriceLabelView.setText(FormatUtils.formatPrice(data.getPrice()));
            }

            TextViewHelper.setText(holder.mTitleTv, data.getTitle(), "嘛玩意儿?");

            try {
                TextViewHelper.setText(holder.mPubDateTv, FuzzyDateFormater.getParsedDate(getContext(), data.getTime()), "一千年以前");
            } catch (Exception e) {
                e.printStackTrace();
            }
            shotBuilder.load(data.getCoverUrl()).into(holder.mShotIv);


            holder.mShotIv.setOnClickListener(v -> {
                accountMangerLazy.get().getCurrentUser()
                        .subscribe(player -> {
                            data.setUser(player);
                            DetailActivity.start(getContext(), data);
                        });

            });
        }

        public void appendAll(List<Goods> items) {
            int startPosition = mDataSet.size();
            mDataSet.addAll(items);
            notifyItemRangeInserted(startPosition, items.size());
        }

        public void resetAll(List<Goods> items) {
            mDataSet.clear();
            mDataSet.addAll(items);
            notifyItemRangeInserted(0, items.size());
        }

        @Override
        public long getItemId(int position) {
            if (mDataSet != null && mDataSet.get(position) != null) {
                return mDataSet.get(position).getId();
            }
            return RecyclerView.NO_ID;
        }

        @Override
        public int getItemCount() {
            return mDataSet == null ? 0 : mDataSet.size();
        }

        public class ManagerItemHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.shot_iv)
            RatioImageView mShotIv;
            @BindView(R.id.price_label_view)
            LabelView mPriceLabelView;
            @BindView(R.id.title_tv)
            TextView mTitleTv;
            @BindView(R.id.delete_btn)
            ImageView mDeleteBtn;
            @BindView(R.id.pub_date_tv)
            TextView mPubDateTv;

            public ManagerItemHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

}
