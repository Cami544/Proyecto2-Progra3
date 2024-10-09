package pos.presentation.estadistica;

public class Rango {

    private int annosDesde;
    private int annosHasta;
    private int mesDesde;
    private int mesHasta;

    public Rango(int annosDesde, int mesDesde, int annosHasta, int mesHasta) {
        this.annosDesde = annosDesde;
        this.mesDesde = mesDesde;
        this.annosHasta = annosHasta;
        this.mesHasta = mesHasta;
    }

    public int getAnnosDesde() {
        return annosDesde;
    }

    public void setAnnosDesde(int annosDesde) {
        this.annosDesde = annosDesde;
    }

    public int getAnnosHasta() {
        return annosHasta;
    }

    public void setAnnosHasta(int annosHasta) {
        this.annosHasta = annosHasta;
    }

    public int getMesDesde() {
        return mesDesde;
    }

    public void setMesDesde(int mesDesde) {
        this.mesDesde = mesDesde;
    }

    public int getMesHasta() {
        return mesHasta;
    }

    public void setMesHasta(int mesHasta) {
        this.mesHasta = mesHasta;
    }

    @Override
    public String toString() {
        return "Rango{" +
                "annosDesde=" + annosDesde +
                ", mesDesde=" + mesDesde +
                ", annosHasta=" + annosHasta +
                ", mesHasta=" + mesHasta +
                '}';
    }

    public boolean isValid() {
        if (annosHasta < annosDesde) {
            return false;
        }
        if (annosHasta == annosDesde && mesHasta < mesDesde) {
            return false;
        }
        if (mesDesde < 1 || mesDesde > 12 || mesHasta < 1 || mesHasta > 12) {
            return false;
        }
        return true;
    }
}
