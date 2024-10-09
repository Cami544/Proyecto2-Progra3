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

import javax.swing.table.TableColumnModel;
import java.util.List;

public class Controller {
    View view;
    Model model;
    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        List<Factura> initialList = Service.instance().search(new Factura());
        model.init(initialList);
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
            List<Factura> facturasFiltradas = Service.instance().searchFacturasByCliente(filter);
            model.setList(facturasFiltradas);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void clear() {
        model.setMode(Application.MODE_CREATE);
        model.setCurrent(new Factura());
    }


    public void updateFacturasTable() {
        List<Factura> facturas = model.getList();

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

    }



    public void updateLineasTable(Factura factura) {
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

        TableColumnModel columnModel =  view.listLineas.getColumnModel();
        columnModel.getColumn(1).setPreferredWidth(150);
        columnModel.getColumn(4).setPreferredWidth(150);
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
            body.addCell(getCell(new Paragraph(e.getNumero()), TextAlignment.CENTER,true));
            body.addCell(getCell(new Paragraph(String.valueOf(e.getCliente())),TextAlignment.CENTER,true));
            body.addCell(getCell(new Paragraph(String.valueOf(e.getCajero())), TextAlignment.CENTER,true));
            body.addCell(getCell(new Paragraph(String.valueOf(e.getFecha())), TextAlignment.CENTER,true));
            body.addCell(getCell(new Paragraph(String.valueOf(e.precioTotalPagar())), TextAlignment.CENTER,true));
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
