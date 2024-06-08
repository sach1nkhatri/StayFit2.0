package com.example.stayfit20;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdaptor extends RecyclerView.Adapter<TaskAdaptor.NoteViewHolder> {
    private OnItemClickListener listener;
    private final List<NoteModel> notes;
    private final SimpleDateFormat inputFormat;
    private final SimpleDateFormat outputFormat;

    public TaskAdaptor(List<NoteModel> notes) {
        this.notes = notes;
        inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()); // Example input format
        outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()); // Desired output format
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
            timestampTextView.setText(formatTimestamp(note.getTimestamp()));

            // Set the onClickListener to open TaskDetail with the relevant data
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), TaskDetail.class);
                intent.putExtra("title", note.getTitle());
                intent.putExtra("description", note.getDescription());
                intent.putExtra("docId", note.getDocId());  // Assuming docId is a property of NoteModel
                itemView.getContext().startActivity(intent);
            });
        }

        private String formatTimestamp(String timestamp) {
            try {
                Date date = inputFormat.parse(timestamp);
                return outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return timestamp; // If parsing fails, return the original timestamp
            }
        }
    }

    public NoteModel getNoteAtPosition(int position) {
        return notes.get(position);
    }

    public void deleteItem(int position) {
        notes.remove(position);
        notifyItemRemoved(position);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
