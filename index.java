import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

// Classe Reserva
class Reserva {
    private String nomeHotel;
    private int numeroQuarto;
    private LocalDate dataCheckIn;
    private LocalDate dataCheckOut;

    public Reserva(String nomeHotel, int numeroQuarto, LocalDate dataCheckIn, LocalDate dataCheckOut) {
        this.nomeHotel = nomeHotel;
        this.numeroQuarto = numeroQuarto;
        this.dataCheckIn = dataCheckIn;
        this.dataCheckOut = dataCheckOut;
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "nomeHotel='" + nomeHotel + '\'' +
                ", numeroQuarto=" + numeroQuarto +
                ", dataCheckIn=" + dataCheckIn +
                ", dataCheckOut=" + dataCheckOut +
                '}';
    }
}

// Classe HashNode
class HashNode {
    String key;
    Reserva value;
    HashNode next;

    public HashNode(String key, Reserva value) {
        this.key = key;
        this.value = value;
        this.next = null;
    }
}

// Classe CustomHashMap
class CustomHashMap {
    private LinkedList<HashNode>[] buckets;
    private int numBuckets;
    private int size;

    @SuppressWarnings("unchecked")
    public CustomHashMap(int numBuckets) {
        this.numBuckets = numBuckets;
        this.buckets = new LinkedList[numBuckets];
        this.size = 0;

        for (int i = 0; i < numBuckets; i++) {
            buckets[i] = new LinkedList<>();
        }
    }

    private int hash(String key) {
        int hash = 0;
        for (char c : key.toCharArray()) {
            hash = (hash + c) % numBuckets;
        }
        return hash;
    }

    public void put(String key, Reserva value) {
        int bucketIndex = hash(key);
        for (HashNode node : buckets[bucketIndex]) {
            if (node.key.equals(key)) {
                node.value = value;
                return;
            }
        }
        HashNode newNode = new HashNode(key, value);
        buckets[bucketIndex].add(newNode);
        size++;
    }

    public Reserva get(String key) {
        int bucketIndex = hash(key);
        for (HashNode node : buckets[bucketIndex]) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    public Reserva remove(String key) {
        int bucketIndex = hash(key);
        HashNode prev = null;
        for (HashNode node : buckets[bucketIndex]) {
            if (node.key.equals(key)) {
                if (prev != null) {
                    prev.next = node.next;
                } else {
                    buckets[bucketIndex].remove(node);
                }
                size--;
                return node.value;
            }
            prev = node;
        }
        return null;
    }

    public void display() {
        for (int i = 0; i < numBuckets; i++) {
            System.out.print("Bucket " + i + ": ");
            for (HashNode node : buckets[i]) {
                System.out.print(node.value + " -> ");
            }
            System.out.println("null");
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}

// Classe para Carregar Reservas
class ReservaLoader {
    public static CustomHashMap carregarReservas(String filePath) {
        CustomHashMap hashMap = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            if (line != null) {
                int numBuckets = Integer.parseInt(line.trim());
                hashMap = new CustomHashMap(numBuckets);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String codigoReserva = parts[0].trim();
                    String nomeHotel = parts[1].trim();
                    int numeroQuarto = Integer.parseInt(parts[2].trim());
                    LocalDate dataCheckIn = LocalDate.parse(parts[3].trim(), formatter);
                    LocalDate dataCheckOut = LocalDate.parse(parts[4].trim(), formatter);

                    Reserva reserva = new Reserva(nomeHotel, numeroQuarto, dataCheckIn, dataCheckOut);
                    hashMap.put(codigoReserva, reserva);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hashMap;
    }
}

// Classe Principal Main
public class Main {
    public static void main(String[] args) {
        String filePath = "reservas.txt";
        CustomHashMap sistema = ReservaLoader.carregarReservas(filePath);

        if (sistema != null) {
            // Exibir a configuração da hashmap
            sistema.display();

            // Exemplo de uso do sistema de reservas
            Reserva novaReserva = new Reserva("Hotel Exemplo", 101, LocalDate.of(2024, 5, 20), LocalDate.of(2024, 5, 25));
            sistema.put("ABC123", novaReserva);

            Reserva recuperada = sistema.get("ABC123");
            if (recuperada != null) {
                System.out.println("Reserva Recuperada: " + recuperada);
            }

            sistema.remove("ABC123");
            sistema.display();
        } else {
            System.out.println("Falha ao carregar o sistema de reservas.");
        }
    }
}
