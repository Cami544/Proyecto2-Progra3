

package pos;

import pos.logic.Service;
import pos.presentation.historico.Controller;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class Application {

        public static void main(String[] args) throws Exception {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception ex) {
            }

            window = new JFrame();
            JTabbedPane tabbedPane = new JTabbedPane();
            window.setContentPane(tabbedPane);

            window.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);
                    Service.instance().stop();
                }
            });



            //cliente
            pos.presentation.clientes.Model clientesModel = new pos.presentation.clientes.Model();
            pos.presentation.clientes.View clientesView = new pos.presentation.clientes.View();
            clientesController = new pos.presentation.clientes.Controller(clientesView, clientesModel);
            Icon clientesIcon = new ImageIcon(Application.class.getResource("/pos/presentation/icons/client.png"));


            tabbedPane.addTab(" Clientes  ", clientesIcon, clientesView.getPanel());

            //Cajero
            pos.presentation.cajeros.Model cajerosModel1 = new pos.presentation.cajeros.Model();
            pos.presentation.cajeros.View cajerosView1 = new pos.presentation.cajeros.View();
            cajerosController = new pos.presentation.cajeros.Controller(cajerosView1, cajerosModel1);
            Icon cajeroIcon = new ImageIcon(Application.class.getResource("/pos/presentation/icons/cajero.png"));

            tabbedPane.addTab(" Cajero", cajeroIcon, cajerosView1.getPanel());

            //Producto tap
            pos.presentation.productos.Model productosModel = new pos.presentation.productos.Model();
            pos.presentation.productos.View productosView = new pos.presentation.productos.View();
            productosController = new pos.presentation.productos.Controller(productosView, productosModel);
            Icon productoIcon = new ImageIcon(Application.class.getResource("/pos/presentation/icons/productos.png"));

            tabbedPane.addTab(" Producto  ", productoIcon, productosView.getPanel1());

            //Factura

            pos.presentation.facturas.Model facturasModel = new pos.presentation.facturas.Model();
            pos.presentation.facturas.View facturasView = new pos.presentation.facturas.View();
            facturasController = new pos.presentation.facturas.Controller(facturasView, facturasModel);
            Icon facturasIcon = new ImageIcon(Application.class.getResource("/pos/presentation/icons/facturaIcon.png"));
            tabbedPane.addTab(" Facturas  ", facturasIcon, facturasView.getPanel());


            //historico

            pos.presentation.historico.Model historicoModel = new pos.presentation.historico.Model();
            pos.presentation.historico.View historicoView = new pos.presentation.historico.View();
            historicoController = new pos.presentation.historico.Controller(historicoView, historicoModel);
            Icon historicoIcon = new ImageIcon(Application.class.getResource("/pos/presentation/icons/historial.png"));


            tabbedPane.addTab(" Historico  ", historicoIcon, historicoView.getPanel());

            //EStadistica

            pos.presentation.estadistica.Model estadisticaModel = new pos.presentation.estadistica.Model();
            pos.presentation.estadistica.View estadisticaView = new pos.presentation.estadistica.View();
            estadisticaController = new pos.presentation.estadistica.Controller(estadisticaView, estadisticaModel);
            Icon estadisticaIcon = new ImageIcon(Application.class.getResource("/pos/presentation/icons/estadistica.png"));

            tabbedPane.addTab(" Estadisticas  ", estadisticaIcon, estadisticaView.getPanel());


            window.setSize(900, 450);
            window.setResizable(false);
            window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            window.setIconImage((new ImageIcon(Application.class.getResource("/pos/presentation/icons/tienda.png"))).getImage());
            window.setTitle("POS: Point Of Sale");
            window.setLocationRelativeTo(null); //para centrar

            window.setVisible(true);


        }

        public static pos.presentation.clientes.Controller clientesController;
        public static pos.presentation.cajeros.Controller cajerosController;
        public static pos.presentation.productos.Controller productosController;
        public static pos.presentation.facturas.Controller facturasController;
        public static pos.presentation.historico.Controller historicoController;
        public static pos.presentation.estadistica.Controller estadisticaController;


        public static JFrame window;

        public final static int MODE_CREATE = 1;
        public final static int MODE_EDIT = 2;

        public static Border BORDER_ERROR = BorderFactory.createMatteBorder(0, 0, 2, 0, Color.RED);
    }

