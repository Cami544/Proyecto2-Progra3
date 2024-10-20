package pos.presentation.historico;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import pos.Application;
import pos.logic.*;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Controller {
    View view;
    Model model;
    public Controller(View view, Model model) throws Exception {
        this.view = view;
        this.model = model;
        List<Factura> facturas = Service.instance().obtenerTodasFacturas();
        model.init(facturas);
        view.setController(this);
        view.setModel(model);
        updateFacturasTable();
    }

    public void search(Factura filter) throws  Exception{
        model.setFilter(filter);
        model.setMode(Application.MODE_CREATE);
        model.setCurrent(new Factura());
        model.setList(Service.instance().search(model.getFilter()));
    }
    public void searchByClienteNombre(String nombreCliente) {
        try {
            Factura filter = new Factura();
            Cliente cliente = new Cliente();
            cliente.setNombre(nombreCliente);
            filter.setCliente(cliente);
            List<Factura> facturas = Service.instance().obtenerFacturasDeCliente(filter);
            model.setList(facturas);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void clear() {
        model.setMode(Application.MODE_CREATE);
        model.setCurrent(new Factura());
    }

    public void updateFacturasTable() throws Exception {
        List<Factura> facturas = model.getList();
        if (facturas == null || facturas.isEmpty()) {
            System.out.println("No hay facturas para mostrar.");
            return;
        }
        int[] cols = {
                TableModel.NUMERO,
                TableModel.NOMBRECliente,
                TableModel.NOMBRECAJERO,
                TableModel.FECHA,
                TableModel.IMPORTE
        };
        TableModel tableModel = new TableModel(cols, facturas);
        view.listFacturas.setModel(tableModel);
        view.listFacturas.setRowHeight(30);
        TableColumnModel columnModel = view.listFacturas.getColumnModel();
        columnModel.getColumn(1).setPreferredWidth(150);
        columnModel.getColumn(4).setPreferredWidth(150);
        System.out.println("Facturas actualizadas correctamente.");
    }

    public void updateLineasTable(Factura factura) {
        if (factura != null && factura.getLineas() != null && !factura.getLineas().isEmpty()) {
            List<Linea> lineas = factura.getLineas();
            int[] cols = {
                    TableModelLineas.NUMERO,
                    TableModelLineas.ARTICULO,
                    TableModelLineas.CATEGORIA,
                    TableModelLineas.CANTIDAD,
                    TableModelLineas.PRECIO,
                    TableModelLineas.DESCUENTO,
                    TableModelLineas.NETO,
                    TableModelLineas.IMPORTE
            };
            TableModelLineas tableModelLineas = new TableModelLineas(cols, lineas);
            view.listLineas.setModel(tableModelLineas);
            view.listLineas.setRowHeight(30);

            TableColumnModel columnModel = view.listLineas.getColumnModel();
            columnModel.getColumn(1).setPreferredWidth(150);
            columnModel.getColumn(4).setPreferredWidth(150);

            System.out.println("Líneas de la factura actualizadas.");
        } else {
            System.out.println("La factura no tiene líneas para mostrar.");
        }
    }

    public void actualizarDatosFacturas() {
        try {
            model.setList(Collections.emptyList());
            List<Factura> facturas = new ArrayList<>(Service.instance().obtenerTodasFacturas());
            if (facturas != null) {
                model.setList(facturas);  // Sobreescribe la lista
                System.out.println("Datos de facturas actualizados correctamente.");
            } else {
                System.out.println("Error: La lista de facturas es nula.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al actualizar los datos de las facturas: " + e.getMessage());
        }
    }




    public <PdfFont> void print()throws Exception{
        String dest="historico.pdf";
        PdfFont font = (PdfFont) PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.setMargins(20, 20, 20, 20);

        Table header = new Table(1);
        header.setWidth(400);
        header.setHorizontalAlignment(HorizontalAlignment.CENTER);
        header.addCell(getCell(new Paragraph("Listado del Historico").setFont((com.itextpdf.kernel.font.PdfFont) font).setBold().setFontSize(20f), TextAlignment.CENTER,false));
        document.add(header);

        document.add(new Paragraph(""));document.add(new Paragraph(""));

        Color bkg = ColorConstants.ORANGE;
        Color frg= ColorConstants.WHITE;
        Table body = new Table(5);
        body.setWidth(400);
        body.setHorizontalAlignment(HorizontalAlignment.CENTER);
        body.addCell(getCell(new Paragraph("Numero").setBackgroundColor(bkg).setFontColor(frg),TextAlignment.CENTER,true));
        body.addCell(getCell(new Paragraph("Cliente").setBackgroundColor(bkg).setFontColor(frg),TextAlignment.CENTER,true));
        body.addCell(getCell(new Paragraph("Cajero").setBackgroundColor(bkg).setFontColor(frg),TextAlignment.CENTER,true));
        body.addCell(getCell(new Paragraph("Fecha").setBackgroundColor(bkg).setFontColor(frg),TextAlignment.CENTER,true));
        body.addCell(getCell(new Paragraph("Total").setBackgroundColor(bkg).setFontColor(frg),TextAlignment.CENTER,true));


        for(Factura e: model.getList()){
            body.addCell(getCell(new Paragraph(String.valueOf(e.getNumero())), TextAlignment.CENTER,true));
            body.addCell(getCell(new Paragraph(String.valueOf(e.getCliente())),TextAlignment.CENTER,true));
            body.addCell(getCell(new Paragraph(String.valueOf(e.getCajero())), TextAlignment.CENTER,true));
            body.addCell(getCell(new Paragraph(String.valueOf(e.getFecha())), TextAlignment.CENTER,true));
            body.addCell(getCell(new Paragraph(String.valueOf(e.precioTotalAPagar())), TextAlignment.CENTER,true));
        }
        document.add(body);
        document.close();
    }

    private Cell getCell(Paragraph paragraph, TextAlignment alignment, boolean hasBorder) {
        Cell cell = new Cell().add(paragraph);
        cell.setPadding(0);
        cell.setTextAlignment(alignment);
        if(!hasBorder) cell.setBorder(Border.NO_BORDER);
        return cell;
    }

    private Cell getCell(Image image, HorizontalAlignment alignment, boolean hasBorder) {
        Cell cell = new Cell().add(image);
        image.setHorizontalAlignment(alignment);
        cell.setPadding(0);
        if(!hasBorder) cell.setBorder(Border.NO_BORDER);
        return cell;
    }

}
