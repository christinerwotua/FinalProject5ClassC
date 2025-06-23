package WithAudioandImages;

import java.util.ArrayList;
import java.util.Random;

/**
 * Kelas Bot untuk menangani logika langkah acak bot
 */
public class Bot {
    // Fungsi untuk memilih langkah acak untuk bot
    public static int[] getMove(Board board) {
        ArrayList<int[]> availableMoves = new ArrayList<>();
        // Cek semua sel kosong dan simpan posisinya
        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                if (board.cells[row][col].content == Seed.NO_SEED) {
                    availableMoves.add(new int[]{row, col});
                }
            }
        }

        // Pilih langkah secara acak dari sel-sel yang kosong
        if (!availableMoves.isEmpty()) {
            Random rand = new Random();
            return availableMoves.get(rand.nextInt(availableMoves.size()));
        }

        return null;  // Jika tidak ada langkah yang tersedia
    }
}

