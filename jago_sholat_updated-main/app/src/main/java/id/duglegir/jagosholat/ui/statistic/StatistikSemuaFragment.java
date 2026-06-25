package id.duglegir.jagosholat.ui.statistic;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import id.duglegir.jagosholat.util.FunctionHelper;
import id.duglegir.jagosholat.model.DataContract;
import id.duglegir.jagosholat.model.DataOperation;
import id.duglegir.jagosholat.model.DatabaseHelper; // Import DatabaseHelper
import id.duglegir.jagosholat.R;
import id.duglegir.jagosholat.databinding.FragmentStatistikSemuaBinding;

public class StatistikSemuaFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DATA_LOADER = 0;

    private AlertDialog.Builder dialog;
    private View dialogView;
    private TextView txt_waktu;

    private FunctionHelper functionHelper = new FunctionHelper();
    private DataOperation crud = new DataOperation();
    private StatistikSemuaCursorAdapter mCursorAdapter;

    private FragmentStatistikSemuaBinding binding;

    public StatistikSemuaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStatistikSemuaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Setup List Adapter
        binding.listViewStatistik.setEmptyView(binding.emptyViews);
        mCursorAdapter = new StatistikSemuaCursorAdapter(requireContext(), null);
        binding.listViewStatistik.setAdapter(mCursorAdapter);

        // 2. Setup Klik Item List (Fitur Update Waktu - Kode Lama)
        binding.listViewStatistik.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                int idColoumnIndex = cursor.getColumnIndex(DataContract.DataEntry._ID);
                int waktuColoumnIndex = cursor.getColumnIndex(DataContract.DataEntry.COLUMN_WAKTU);
                String getArrayId = cursor.getString(idColoumnIndex);
                String getArrayWaktu = cursor.getString(waktuColoumnIndex);

                DialogForm(getArrayId, getArrayWaktu);
            }
        });

        // 3. Setup Tombol Hapus Semua (Fitur Baru)
        binding.fabHapusSemua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tampilkanDialogHapusSemua();
            }
        });

        // 4. Init Loader
        LoaderManager.getInstance(this).initLoader(DATA_LOADER, null, this);
    }

    // --- LOGIKA HAPUS SEMUA DATA (BARU) ---
    private void tampilkanDialogHapusSemua() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Hapus Seluruh Data?")
                .setMessage("PERINGATAN: Semua riwayat sholat akan dihapus permanen. Tindakan ini tidak bisa dibatalkan.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("HAPUS SEMUA", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        eksekusiHapusData();
                    }
                })
                .setNegativeButton("BATAL", null)
                .show();
    }

    private void eksekusiHapusData() {
        // Panggil DatabaseHelper untuk menghapus
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        dbHelper.hapusSemuaData();

        // Restart Loader agar tampilan list langsung kosong (refresh)
        LoaderManager.getInstance(this).restartLoader(DATA_LOADER, null, this);

        Toast.makeText(requireContext(), "Semua data berhasil dihapus", Toast.LENGTH_SHORT).show();
    }
    // ----------------------------------------

    // --- LOGIKA UPDATE DATA (LAMA) ---
    private void DialogForm(final String mID, String mWaktu) {
        dialog = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.content_statistik_update, null);
        txt_waktu = dialogView.findViewById(R.id.txt_waktu_update);

        dialog.setView(dialogView);
        dialog.setCancelable(true);
        txt_waktu.setText(mWaktu);

        txt_waktu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog mTimePickerDialog = new TimePickerDialog(requireActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        functionHelper.getFormatTimePicker(txt_waktu, hourOfDay, minute);
                    }
                }, functionHelper.getSystemJam(), functionHelper.getSystemMenit(), true);

                mTimePickerDialog.show();
            }
        });

        dialog.setPositiveButton("CATAT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String tempWaktu = txt_waktu.getText().toString();
                String selection = DataContract.DataEntry._ID + " = '" + mID + "'";

                boolean isUpdated = crud.updateDataWaktu(requireContext(), tempWaktu, selection, null);
                if (isUpdated) {
                    Toast.makeText(requireContext(), "Waktu Telah Diubah", Toast.LENGTH_LONG).show();
                    // Restart loader untuk melihat perubahan waktu
                    LoaderManager.getInstance(StatistikSemuaFragment.this).restartLoader(DATA_LOADER, null, StatistikSemuaFragment.this);
                } else {
                    Toast.makeText(requireContext(), "Data Not Updated", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(requireActivity(),
                DataContract.DataEntry.CONTENT_URI,
                crud.getProjection(),
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}