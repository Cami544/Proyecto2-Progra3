package pos.presentation.estadistica;

import java.io.*;

public class MatrizDataSer {


    public static void serializarMatriz(Float[][] matriz, String archivo) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(archivo))) {
            out.writeObject(matriz);
        }
    }
    public static Float[][] deserializarMatriz(String archivo) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
            return (Float[][]) in.readObject();
        }
    }
}
