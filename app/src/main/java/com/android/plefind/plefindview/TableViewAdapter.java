package com.android.plefind.plefindview;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;

public class TableViewAdapter extends RecyclerView.Adapter {

    List<TableRow> tableRowList;
    private static Vector<Integer> selected = new Vector<>();

    public static Vector<Integer> getSelected() {
        return selected;
    }

    public TableViewAdapter(List<TableRow> tableRowList) {
        this.tableRowList = tableRowList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.table_list_item, parent, false);

        selected = new Vector<>();

        return new RowViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final RowViewHolder rowViewHolder = (RowViewHolder) holder;
        final int rowPos = rowViewHolder.getAdapterPosition();

        if (rowPos == 0) {
            // Header Cells. Main Headings appear here
            rowViewHolder.txtName.setBackgroundResource(R.drawable.table_header_cell_bg);
            rowViewHolder.txtDateMesurement.setBackgroundResource(R.drawable.table_header_cell_bg);
            rowViewHolder.txtNotes.setBackgroundResource(R.drawable.table_header_cell_bg);
            rowViewHolder.txtDiagnosis.setBackgroundResource(R.drawable.table_header_cell_bg);
            rowViewHolder.txtPleuralEffusion.setBackgroundResource(R.drawable.table_header_cell_bg);
            rowViewHolder.txtThoracocentesis.setBackgroundResource(R.drawable.table_header_cell_bg);
            rowViewHolder.txtLung.setBackgroundResource(R.drawable.table_header_cell_bg);
            rowViewHolder.txtSex.setBackgroundResource(R.drawable.table_header_cell_bg);
            rowViewHolder.txtAge.setBackgroundResource(R.drawable.table_header_cell_bg);
            rowViewHolder.txtHeight.setBackgroundResource(R.drawable.table_header_cell_bg);
            rowViewHolder.txtWeight.setBackgroundResource(R.drawable.table_header_cell_bg);

            rowViewHolder.txtName.setText("Name");
            rowViewHolder.txtDateMesurement.setText("Date");
            rowViewHolder.txtNotes.setText("Notes");
            rowViewHolder.txtDiagnosis.setText("Diagnosis");
            rowViewHolder.txtPleuralEffusion.setText("Pleural effusion");
            rowViewHolder.txtPleuralEffusion.setPadding(0,0,0,0);
            rowViewHolder.txtThoracocentesis.setText("Thoracocentesis");
            rowViewHolder.txtLung.setText("Lung");
            rowViewHolder.txtSex.setText("Sex");
            rowViewHolder.txtAge.setText("Birthdate");
            rowViewHolder.txtHeight.setText("Height");
            rowViewHolder.txtWeight.setText("Weight");
        } else {
            TableRow tableRow = tableRowList.get(rowPos-1);

            // Content Cells. Content appear here
            rowViewHolder.txtName.setBackgroundResource(R.drawable.table_content_cell_bg);
            rowViewHolder.txtName.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                        System.out.println("up " + rowPos);
                        if (selected.contains(rowPos)){
                            rowViewHolder.setUnselected();
                            selected.removeElement(rowPos);
                            System.out.println(selected.toString());
                        } else {
                            rowViewHolder.setSelected();
                            selected.add(rowPos);
                            System.out.println(selected.toString());
                        }
                        if (selected.size() > 0){
                            CloudFragment.select.setEnabled(true);
                            CloudFragment.select.setAlpha(1.f);
                        } else {
                            CloudFragment.select.setEnabled(false);
                            CloudFragment.select.setAlpha(.2f);
                        }
                    }
                    return true;
                }
            });
            rowViewHolder.txtDateMesurement.setBackgroundResource(R.drawable.table_content_cell_bg);
            rowViewHolder.txtDateMesurement.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                        System.out.println("up " + rowPos);
                        if (selected.contains(rowPos)){
                            rowViewHolder.setUnselected();
                            selected.removeElement(rowPos);
                            System.out.println(selected.toString());
                        } else {
                            rowViewHolder.setSelected();
                            selected.add(rowPos);
                            System.out.println(selected.toString());
                        }
                        if (selected.size() > 0){
                            CloudFragment.select.setEnabled(true);
                            CloudFragment.select.setAlpha(1.f);
                        } else {
                            CloudFragment.select.setEnabled(false);
                            CloudFragment.select.setAlpha(.2f);
                        }
                    }
                    return true;
                }
            });
            rowViewHolder.txtNotes.setBackgroundResource(R.drawable.table_content_cell_bg);
            rowViewHolder.txtNotes.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                        System.out.println("up " + rowPos);
                        if (selected.contains(rowPos)){
                            rowViewHolder.setUnselected();
                            selected.removeElement(rowPos);
                            System.out.println(selected.toString());
                        } else {
                            rowViewHolder.setSelected();
                            selected.add(rowPos);
                            System.out.println(selected.toString());
                        }
                        if (selected.size() > 0){
                            CloudFragment.select.setEnabled(true);
                            CloudFragment.select.setAlpha(1.f);
                        } else {
                            CloudFragment.select.setEnabled(false);
                            CloudFragment.select.setAlpha(.2f);
                        }
                    }
                    return true;
                }
            });
            rowViewHolder.txtDiagnosis.setBackgroundResource(R.drawable.table_content_cell_bg);
            rowViewHolder.txtDiagnosis.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                        System.out.println("up " + rowPos);
                        if (selected.contains(rowPos)){
                            rowViewHolder.setUnselected();
                            selected.removeElement(rowPos);
                            System.out.println(selected.toString());
                        } else {
                            rowViewHolder.setSelected();
                            selected.add(rowPos);
                            System.out.println(selected.toString());
                        }
                        if (selected.size() > 0){
                            CloudFragment.select.setEnabled(true);
                            CloudFragment.select.setAlpha(1.f);
                        } else {
                            CloudFragment.select.setEnabled(false);
                            CloudFragment.select.setAlpha(.2f);
                        }
                    }
                    return true;
                }
            });
            rowViewHolder.txtPleuralEffusion.setBackgroundResource(R.drawable.table_content_cell_bg);
            rowViewHolder.txtPleuralEffusion.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                        System.out.println("up " + rowPos);
                        if (selected.contains(rowPos)){
                            rowViewHolder.setUnselected();
                            selected.removeElement(rowPos);
                            System.out.println(selected.toString());
                        } else {
                            rowViewHolder.setSelected();
                            selected.add(rowPos);
                            System.out.println(selected.toString());
                        }
                        if (selected.size() > 0){
                            CloudFragment.select.setEnabled(true);
                            CloudFragment.select.setAlpha(1.f);
                        } else {
                            CloudFragment.select.setEnabled(false);
                            CloudFragment.select.setAlpha(.2f);
                        }
                    }
                    return true;
                }
            });
            rowViewHolder.txtThoracocentesis.setBackgroundResource(R.drawable.table_content_cell_bg);
            rowViewHolder.txtThoracocentesis.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                        System.out.println("up " + rowPos);
                        if (selected.contains(rowPos)){
                            rowViewHolder.setUnselected();
                            selected.removeElement(rowPos);
                            System.out.println(selected.toString());
                        } else {
                            rowViewHolder.setSelected();
                            selected.add(rowPos);
                            System.out.println(selected.toString());
                        }
                        if (selected.size() > 0){
                            CloudFragment.select.setEnabled(true);
                            CloudFragment.select.setAlpha(1.f);
                        } else {
                            CloudFragment.select.setEnabled(false);
                            CloudFragment.select.setAlpha(.2f);
                        }
                    }
                    return true;
                }
            });
            rowViewHolder.txtLung.setBackgroundResource(R.drawable.table_content_cell_bg);
            rowViewHolder.txtLung.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                        System.out.println("up " + rowPos);
                        if (selected.contains(rowPos)){
                            rowViewHolder.setUnselected();
                            selected.removeElement(rowPos);
                            System.out.println(selected.toString());
                        } else {
                            rowViewHolder.setSelected();
                            selected.add(rowPos);
                            System.out.println(selected.toString());
                        }
                        if (selected.size() > 0){
                            CloudFragment.select.setEnabled(true);
                            CloudFragment.select.setAlpha(1.f);
                        } else {
                            CloudFragment.select.setEnabled(false);
                            CloudFragment.select.setAlpha(.2f);
                        }
                    }
                    return true;
                }
            });
            rowViewHolder.txtSex.setBackgroundResource(R.drawable.table_content_cell_bg);
            rowViewHolder.txtSex.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                        System.out.println("up " + rowPos);
                        if (selected.contains(rowPos)){
                            rowViewHolder.setUnselected();
                            selected.removeElement(rowPos);
                            System.out.println(selected.toString());
                        } else {
                            rowViewHolder.setSelected();
                            selected.add(rowPos);
                            System.out.println(selected.toString());
                        }
                        if (selected.size() > 0){
                            CloudFragment.select.setEnabled(true);
                            CloudFragment.select.setAlpha(1.f);
                        } else {
                            CloudFragment.select.setEnabled(false);
                            CloudFragment.select.setAlpha(.2f);
                        }
                    }
                    return true;
                }
            });
            rowViewHolder.txtAge.setBackgroundResource(R.drawable.table_content_cell_bg);
            rowViewHolder.txtAge.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                        System.out.println("up " + rowPos);
                        if (selected.contains(rowPos)){
                            rowViewHolder.setUnselected();
                            selected.removeElement(rowPos);
                            System.out.println(selected.toString());
                        } else {
                            rowViewHolder.setSelected();
                            selected.add(rowPos);
                            System.out.println(selected.toString());
                        }
                        if (selected.size() > 0){
                            CloudFragment.select.setEnabled(true);
                            CloudFragment.select.setAlpha(1.f);
                        } else {
                            CloudFragment.select.setEnabled(false);
                            CloudFragment.select.setAlpha(.2f);
                        }
                    }
                    return true;
                }
            });
            rowViewHolder.txtHeight.setBackgroundResource(R.drawable.table_content_cell_bg);
            rowViewHolder.txtHeight.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                        System.out.println("up " + rowPos);
                        if (selected.contains(rowPos)){
                            rowViewHolder.setUnselected();
                            selected.removeElement(rowPos);
                            System.out.println(selected.toString());
                        } else {
                            rowViewHolder.setSelected();
                            selected.add(rowPos);
                            System.out.println(selected.toString());
                        }
                        if (selected.size() > 0){
                            CloudFragment.select.setEnabled(true);
                            CloudFragment.select.setAlpha(1.f);
                        } else {
                            CloudFragment.select.setEnabled(false);
                            CloudFragment.select.setAlpha(.2f);
                        }
                    }
                    return true;
                }
            });
            rowViewHolder.txtWeight.setBackgroundResource(R.drawable.table_content_cell_bg);
            rowViewHolder.txtWeight.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                        System.out.println("up " + rowPos);
                        if (selected.contains(rowPos)){
                            rowViewHolder.setUnselected();
                            selected.removeElement(rowPos);
                            System.out.println(selected.toString());
                        } else {
                            rowViewHolder.setSelected();
                            selected.add(rowPos);
                            System.out.println(selected.toString());
                        }
                        if (selected.size() > 0){
                            CloudFragment.select.setEnabled(true);
                            CloudFragment.select.setAlpha(1.f);
                        } else {
                            CloudFragment.select.setEnabled(false);
                            CloudFragment.select.setAlpha(.2f);
                        }
                    }
                    return true;
                }
            });

            rowViewHolder.txtName.setText(tableRow.getName());
            String strDateMeas = null;
            if (tableRow.getDateMeasurement() != null){
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                strDateMeas = formatter.format(tableRow.getDateMeasurement());
            }
            rowViewHolder.txtDateMesurement.setText(strDateMeas);
            rowViewHolder.txtNotes.setText(tableRow.getNotes());
            rowViewHolder.txtDiagnosis.setText(tableRow.getDiagnosis());
            rowViewHolder.txtPleuralEffusion.setText(tableRow.getHealthy());
            rowViewHolder.txtThoracocentesis.setText(tableRow.getThoraco());
            rowViewHolder.txtLung.setText(tableRow.getLung());
            rowViewHolder.txtSex.setText(tableRow.getSex());
            String strDate = null;
            if (tableRow.getBirthdate() != null){
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                strDate = formatter.format(tableRow.getBirthdate());
            }
            rowViewHolder.txtAge.setText(strDate);
            rowViewHolder.txtHeight.setText(tableRow.getHeight());
            rowViewHolder.txtWeight.setText(tableRow.getWeight());
            //System.out.println("row");
        }
    }

    @Override
    public int getItemCount() {
        return tableRowList.size()+1; // one more to add header row
    }

    public class RowViewHolder extends RecyclerView.ViewHolder {
        protected TextView txtName, txtNotes, txtDiagnosis, txtPleuralEffusion, txtThoracocentesis, txtLung,
                txtSex, txtAge, txtHeight, txtWeight, txtDateMesurement;

        public RowViewHolder(View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            txtDateMesurement = itemView.findViewById(R.id.txtDateMeasurement);
            txtNotes = itemView.findViewById(R.id.txtNotes);
            txtDiagnosis = itemView.findViewById(R.id.txtDiagnosis);
            txtPleuralEffusion = itemView.findViewById(R.id.txtPleuralEffusion);
            txtThoracocentesis = itemView.findViewById(R.id.txtThoracocentesis);
            txtLung = itemView.findViewById(R.id.txtLung);
            txtSex = itemView.findViewById(R.id.txtSex);
            txtAge = itemView.findViewById(R.id.txtAge);
            txtHeight = itemView.findViewById(R.id.txtHeight);
            txtWeight = itemView.findViewById(R.id.txtWeight);
        }

        public void setSelected(){
            txtName.setBackgroundResource(R.drawable.table_content_cell_bg_selected);
            txtDateMesurement.setBackgroundResource(R.drawable.table_content_cell_bg_selected);
            txtNotes.setBackgroundResource(R.drawable.table_content_cell_bg_selected);
            txtDiagnosis.setBackgroundResource(R.drawable.table_content_cell_bg_selected);
            txtPleuralEffusion.setBackgroundResource(R.drawable.table_content_cell_bg_selected);
            txtThoracocentesis.setBackgroundResource(R.drawable.table_content_cell_bg_selected);
            txtLung.setBackgroundResource(R.drawable.table_content_cell_bg_selected);
            txtSex.setBackgroundResource(R.drawable.table_content_cell_bg_selected);
            txtAge.setBackgroundResource(R.drawable.table_content_cell_bg_selected);
            txtHeight.setBackgroundResource(R.drawable.table_content_cell_bg_selected);
            txtWeight.setBackgroundResource(R.drawable.table_content_cell_bg_selected);
        }

        public void setUnselected(){
            txtName.setBackgroundResource(R.drawable.table_content_cell_bg);
            txtDateMesurement.setBackgroundResource(R.drawable.table_content_cell_bg);
            txtNotes.setBackgroundResource(R.drawable.table_content_cell_bg);
            txtDiagnosis.setBackgroundResource(R.drawable.table_content_cell_bg);
            txtPleuralEffusion.setBackgroundResource(R.drawable.table_content_cell_bg);
            txtThoracocentesis.setBackgroundResource(R.drawable.table_content_cell_bg);
            txtLung.setBackgroundResource(R.drawable.table_content_cell_bg);
            txtSex.setBackgroundResource(R.drawable.table_content_cell_bg);
            txtAge.setBackgroundResource(R.drawable.table_content_cell_bg);
            txtHeight.setBackgroundResource(R.drawable.table_content_cell_bg);
            txtWeight.setBackgroundResource(R.drawable.table_content_cell_bg);
        }
    }
}