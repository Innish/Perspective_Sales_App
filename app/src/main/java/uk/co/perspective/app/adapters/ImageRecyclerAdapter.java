package uk.co.perspective.app.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import uk.co.perspective.app.R;
import uk.co.perspective.app.entities.Activity;
import uk.co.perspective.app.objects.CustomImage;
import uk.co.perspective.app.objects.FileItem;

public class ImageRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<FileItem> fileList;
    private Context context;

    RecyclerView mRecyclerView;

    ImageRecyclerAdapter.FileListener mListener;

    private final FragmentManager fragmentManager;

    public interface FileListener {
        public void AddNewFile();
        public void OpenFile(int ID, String path);
        public void RemoveFile(int ID, String path);
    }

    public ImageRecyclerAdapter(Context context, FragmentManager fragmentManager, ArrayList<FileItem> fileList, FileListener listener) {
        this.fileList = fileList;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.mListener = listener;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public int getItemViewType(int position) {

        if (this.fileList.get(position).getTitle().equals("New Image"))
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

        View view;
        RecyclerView.ViewHolder viewHolder;

        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_file, parent, false);
            viewHolder = new ImageRecyclerAdapter.RecordViewHolder(view);
        }
        else
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_add_file, parent, false);
            viewHolder = new ImageRecyclerAdapter.AddRecordViewHolder(view);
        }

        return viewHolder;

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        if (viewHolder.getItemViewType() == 1) {

            ImageRecyclerAdapter.RecordViewHolder holder = (ImageRecyclerAdapter.RecordViewHolder) viewHolder;

            final int mPosition = position;
            final String provider = this.context.getPackageName() + ".provider";

            if (fileList.get(position).getTitle() != null) {
                if (!fileList.get(position).getTitle().equals("")) {
                    holder.fileName.setText(fileList.get(position).getTitle());
                    holder.fileName.setVisibility(View.VISIBLE);
                } else {
                    holder.fileName.setVisibility(View.GONE);
                }
            } else {
                holder.fileName.setVisibility(View.GONE);
            }

            holder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);

            if (fileList.get(position).getImage() != null) {

                holder.icon.setVisibility(View.GONE);
                holder.img.setVisibility(View.VISIBLE);
                holder.fileName.setVisibility(View.GONE);
                holder.deleteButton.setVisibility(View.VISIBLE);
                holder.cloudIcon.setVisibility(View.GONE);
                holder.img.setImageBitmap((fileList.get(position).getImage()));

            }
            else if(fileList.get(position).getFilePath().contains("http") && fileList.get(position).getFilePath().matches("^.*\\.(jpg|JPG|jpeg|JPEG|gif|GIF|png|PNG)$"))
            {
                Glide.with(context).load(fileList.get(position).getFilePath()).into(holder.img);
                holder.icon.setVisibility(View.GONE);
                holder.img.setVisibility(View.VISIBLE);
                holder.deleteButton.setVisibility(View.GONE);
                holder.cloudIcon.setVisibility(View.VISIBLE);
                holder.fileName.setVisibility(View.GONE);
            }
            else {

                if(fileList.get(position).getFilePath().contains("http"))
                {
                    holder.deleteButton.setVisibility(View.GONE);
                    holder.cloudIcon.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.deleteButton.setVisibility(View.VISIBLE);
                    holder.cloudIcon.setVisibility(View.GONE);
                }

                holder.icon.setVisibility(View.VISIBLE);
                holder.img.setVisibility(View.GONE);

                if (fileList.get(position).getFilePath().contains(".msg")) {
                    holder.icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.outlook_icon));
                }
                else if (fileList.get(position).getFilePath().contains(".doc")) {
                    holder.icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.word_icon));
                }
                else if (fileList.get(position).getFilePath().contains(".xls")) {
                    holder.icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.excel_icon));
                }
                else if (fileList.get(position).getFilePath().contains(".ppt")) {
                    holder.icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.powerpoint_icon));
                }
                else if (fileList.get(position).getFilePath().contains(".pdf")) {
                    holder.icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.pdf_icon));
                }
                else {
                    holder.icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_add));
                }
            }

            holder.mView.setClipToOutline(true);

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mListener.OpenFile(fileList.get(position).getID(), fileList.get(position).getFilePath());
                }
            });

            holder.deleteButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mListener.RemoveFile(fileList.get(position).getID(), fileList.get(position).getFilePath());
                }
            });

        }
        else
        {
            ImageRecyclerAdapter.AddRecordViewHolder holder = (ImageRecyclerAdapter.AddRecordViewHolder) viewHolder;
            holder.mView.setClipToOutline(true);

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mListener.AddNewFile();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder{

        public View mView;
        private TextView fileName;
        private ImageView img;
        private ImageView icon;
        private ImageView deleteButton;
        private ImageView cloudIcon;

        public RecordViewHolder(View view) {
            super(view);

            mView = view;
            view.setClipToOutline(true);
            fileName = (TextView)view.findViewById(R.id.file_name);
            img = (CustomImage) view.findViewById(R.id.img);
            icon = (ImageView) view.findViewById(R.id.icon);
//            isNew = (ImageView) view.findViewById(R.id.is_new);
            deleteButton = view.findViewById(R.id.remove_file);
            cloudIcon = view.findViewById(R.id.cloud_file);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + fileName.getText() + "'";
        }

    }

    public static class AddRecordViewHolder extends RecyclerView.ViewHolder {

        public Activity mItem;
        public int thisJournalEntryID;

        public final View mView;
        public final ImageView mAddRecord;

        public AddRecordViewHolder(View view) {
            super(view);
            mView = view;
            mAddRecord = view.findViewById(R.id.add_record);
        }
    }
}
