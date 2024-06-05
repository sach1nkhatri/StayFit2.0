package com.example.stayfit20;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdaptor extends RecyclerView.Adapter<TaskAdaptor.NoteViewHolder> {
    private OnItemClickListener listener;
    private List<NoteModel> notes;

    public TaskAdaptor(List<NoteModel> notes) {
        this.notes = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.taskrecyclerview, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        NoteModel note = notes.get(position);
        holder.bind(note);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView, timestampTextView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.note_title_text_view);
            descriptionTextView = itemView.findViewById(R.id.note_description_text_view);
            timestampTextView = itemView.findViewById(R.id.note_timestamp_text_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(position);
                    }
                }
            });
        }

        public void bind(NoteModel note) {
            titleTextView.setText(note.getTitle());
            descriptionTextView.setText(note.getDescription());
            timestampTextView.setText(note.getTimestamp());

            // Set the onClickListener to open TaskDetail with the relevant data
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), TaskDetail.class);
                intent.putExtra("title", note.getTitle());
                intent.putExtra("description", note.getDescription());
                intent.putExtra("docId", note.getDocId());  // Assuming docId is a property of NoteModel
                itemView.getContext().startActivity(intent);
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
