package ru.geekbrains.gb_android_libraries.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.jakewharton.rxbinding2.view.RxView;
import ru.geekbrains.gb_android_libraries.R;
import ru.geekbrains.gb_android_libraries.mvp.presenter.list.IRepositoryListPresenter;
import ru.geekbrains.gb_android_libraries.mvp.view.list.ReposotoryItemView;

public class RepositoriesRVAdapter extends RecyclerView.Adapter<RepositoriesRVAdapter.ViewHolder> {
    private IRepositoryListPresenter presenter;

    public RepositoriesRVAdapter(IRepositoryListPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_repository, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.pos = position;
        presenter.bind(holder);
        RxView.clicks(holder.itemView).map(o -> holder).subscribe(presenter.getClickSubject());
    }

    @Override
    public int getItemCount() {
        return presenter.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements ReposotoryItemView {
        int pos = 0;

        @BindView(R.id.tv_title)
        TextView titleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public int getPos() {
            return pos;
        }

        @Override
        public void setTitle(String title) {
            titleTextView.setText(title);
        }
    }
}
