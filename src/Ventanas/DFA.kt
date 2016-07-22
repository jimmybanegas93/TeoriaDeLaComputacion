package Ventanas

import Automatas.AutomataDFA
import Automatas.Estado
import com.mxgraph.model.mxCell
import com.mxgraph.swing.mxGraphComponent
import com.mxgraph.util.mxEvent
import com.mxgraph.util.mxEventObject
import com.mxgraph.view.mxGraph
import java.awt.Color
import java.awt.MenuComponent
import java.awt.Point
import java.awt.event.*
import java.util.*
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JOptionPane

/**
 * Created by Affisa-Jimmy on 22/7/2016.
 */
class DFA (automataDFA: AutomataDFA) : JFrame() {
    // Variables declaration - do not modify
    private var jButtonProbarAutomata: javax.swing.JButton? = null
    private var jLabel1: javax.swing.JLabel? = null
    private var jLabel2: javax.swing.JLabel? = null
    private var jLabel3: javax.swing.JLabel? = null
    private var jLabel4: javax.swing.JLabel? = null
    private var jTextFieldAlfabeto: javax.swing.JTextField? = null
    private var jTextFieldCadena: javax.swing.JTextField? = null

    protected var graph = mxGraph()
    private var graphComponent: mxGraphComponent? = null

    protected var contadorEstados = 0
    protected var dfa: AutomataDFA? = null
    // End of variables declaration

    init {
        initComponents()
        dfa = automataDFA
    }

    fun initComponents() {
        jLabel1 = javax.swing.JLabel()
        jLabel3 = javax.swing.JLabel()
        jLabel4 = javax.swing.JLabel()
        jTextFieldAlfabeto = javax.swing.JTextField()
        jTextFieldCadena = javax.swing.JTextField()
        jLabel2 = javax.swing.JLabel()
        jButtonProbarAutomata = javax.swing.JButton()

        defaultCloseOperation = javax.swing.WindowConstants.DISPOSE_ON_CLOSE
        title = "Automata Finito Deterministico"
        contentPane.background = Color.WHITE
        (jButtonProbarAutomata as JButton).setBackground(Color.WHITE)
        graph = mxGraph()
        graph.isAllowLoops = true
        graph.isDisconnectOnMove = false
        graph.isConnectableEdges = false
        graph.isEdgeLabelsMovable = false
        graphComponent = mxGraphComponent(graph)
        (graphComponent as mxGraphComponent).viewport.background = Color.WHITE
        (graphComponent as mxGraphComponent).addKeyListener(object: KeyAdapter() {

            override fun keyReleased(e: KeyEvent) {}

            override fun  keyTyped(e: KeyEvent) { }

            override fun keyPressed(e: KeyEvent){
                // e.keyCode
                if (e.getKeyCode() == KeyEvent.VK_DELETE){
                  //  if (!graph.isSelectionEmpty()) {
                        delFunction()

                        //graph.model.remove(e.component.remove(e.source as MenuComponent?))
                       // bar.getdeleteaction().actionPerformed(null);
                        println("BOORAR")
                    }
            }

            private fun delFunction() {
                graph.model.beginUpdate()
                try {
                    var cells = graph.selectionCell

                    println(cells)
                    //  setEnabledButton(cells, true)
                  /*  val x = graph.getCellBounds(cells[0]).x
                    val y = graph.getCellBounds(cells[0]).y*/
                    var p = Point(x.toInt(), y.toInt())
                    //  cells = graph.getChildCells(cells)
                    graph.model.remove(cells)
                    // statusLabel.setText("Status: Unsaved")
                }finally{
                    graph.model.endUpdate()
                }

            }
        })

        (graphComponent as mxGraphComponent).connectionHandler?.addListener(mxEvent.CONNECT) { sender: Any, evt: mxEventObject ->
                val edge = evt.getProperty("cell") as mxCell

                //Validar que el target de la flecha no sea null, si es nulo o no colisiona con otro estado, lo elimino
                if (edge.target == null  ) {
                    graph.model.remove(evt.getProperty("cell"))
                    return@addListener
                }

               //Los mxCell de orogen y destino
                val origen = edge.getSource() as mxCell
                val destino = edge.getTarget() as mxCell

                //Ahora tengo que ver a que estado pertenecen dichos Objetos del grafico
                val v1 = obtenerEstado(origen)
                val v2 = obtenerEstado(destino)

                if (v2 == null  ) {
                    graph.model.remove(evt.getProperty("cell"))
                    return@addListener
                }

                val nombre = nombrarTransicion()

                if (nombre.toInt() == 0) {
                    graph.model.remove(evt.getProperty("cell"))
                    return@addListener
                }

                var a = dfa?.verificarTransicion(v1 as Estado, nombre)

                 if ((a as Boolean)) {
                     ConfigurationForWindows.messageDialog(contentPane,"No se puede agregar una Transicion con el mismo valor!")
                     graph.model.remove(evt.getProperty("cell"))
                     return@addListener
                 }
                val name = nombre.toString()
                edge.setValue(" "+name+ " ")
                dfa?.agregarTransicion(name,v1 as Estado, v2, edge)

                 dfa.toString()

        }

        (graphComponent as mxGraphComponent).getGraphControl().addMouseListener(object : MouseAdapter() {
            override fun mouseReleased(e: MouseEvent) {
                if (e.isPopupTrigger()) {
                   // jPopupMenuForm.show(contentPane, e.getX(), e.getY())
                }
            }
            override fun mousePressed(e: MouseEvent) {
                // TODO Auto-generated method stub
                if (e.getClickCount() === 2 && e.getButton() === MouseEvent.BUTTON1) {
                    val x = e.getX()
                    val y = e.getY()
                    graph.model.beginUpdate()
                    try {
                        val parent = graph.defaultParent
                        val name = (contadorEstados++).toString()
                        val esDeAceptacion: Boolean

                        val dialogButton2 = JOptionPane.YES_NO_OPTION
                        val aceptacion = JOptionPane.showConfirmDialog(graphComponent,"¿Es estado de aceptación?", "Aceptación", dialogButton2)
                        if (aceptacion == 0) {
                            esDeAceptacion = true
                        } else {
                            esDeAceptacion = false
                        }

                         var v1 = Any()

                        //Para definir el estado inicial si el contador de estados es cero y pintarlo de otro color
                        if(contadorEstados-1==0)
                        {
                            if(esDeAceptacion){
                                //Este sería un estado inicial y de aceptacion a la vez
                                v1 = graph.insertVertex(parent, null, "q" + name, x.toDouble(), y.toDouble(), 50.toDouble(), 50.toDouble(),
                                        "resizable=0;editable=0;shape=doubleEllipse;whiteSpace=wrap;" + "fillColor=lightyellow")

                                //Agregar a lista de estados de aceptacion
                                dfa?.agregarEstadoAceptacion(estado = Estado("q"+name,v1 as Object))
                            }else{

                                //Este sería un estado inicial pero no de aceptacion
                                v1 = graph.insertVertex(parent, null, "q" + name, x.toDouble(), y.toDouble(), 50.toDouble(), 50.toDouble(),
                                        "resizable=0;editable=0;shape=ellipse;whiteSpace=wrap;" + "fillColor=lightyellow")
                            }
                            //Setear el estado inicial a mi DFA, lo haremos sea o no de aceptacion
                            dfa?.estadoInicial= Estado("q"+name,v1 as Object)
                        }
                        else{
                            if(esDeAceptacion){
                                //Este sería un estado no inicial pero de aceptacion
                                v1 = graph.insertVertex(parent, null, "q" + name, x.toDouble(), y.toDouble(), 50.toDouble(), 50.toDouble(),
                                        "resizable=0;editable=0;shape=doubleEllipse;whiteSpace=wrap;" + "fillColor=lightgreen")

                                //Agregar a lista de estados de aceptacion
                                dfa?.agregarEstadoAceptacion(estado = Estado("q"+name,v1 as Object))

                            }else
                            {
                                //Este sería un estado normal, no de aceptacion y no inicial
                                v1 = graph.insertVertex(parent, null, "q" + name, x.toDouble(), y.toDouble(), 50.toDouble(), 50.toDouble(),
                                        "resizable=0;editable=0;shape=ellipse;whiteSpace=wrap;" + "fillColor=lightgreen")
                            }
                        }

                        //Agregar a la lista general de estados
                        dfa?.agregarEstado("q" + name, v1 as Object)

                        //Imprimir
                        println(dfa.toString())

                    } finally {
                        graph.model.endUpdate()
                    }
                }
            }
        })

        (jLabel1 as JLabel).setText("Ingresar alfabeto:");

        (jLabel2 as JLabel).setText("Ingresar cadena:");

      /*(jLabel4 as JLabel).setText("TIP: No olvidar agregar todo el alfabeto. Es posible que no acepte cadena!");

        (jLabel3 as JLabel).setText("PROTIP: Doble-Click para agregar Estado!");*/

        (jButtonProbarAutomata as JButton).setText("Evaluar Automata")
        (jButtonProbarAutomata as JButton).addActionListener({ e: ActionEvent -> evaluarCadena(e) })

        val layout = javax.swing.GroupLayout(contentPane)
        contentPane.layout = layout
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addComponent(jLabel1)
                                .addGap(25, 25, 25)
                                .addComponent(jTextFieldAlfabeto, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(25, 25, 25)
                                .addComponent(jLabel2)
                                .addGap(25, 25, 25)
                                .addComponent(jTextFieldCadena, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(25, 25, 25)
                                .addComponent(jButtonProbarAutomata)
                                .addContainerGap(30, java.lang.Short.MAX_VALUE.toInt()))
                        .addComponent(jLabel4).addComponent(jLabel3)
                        .addComponent(graphComponent, 500, javax.swing.GroupLayout.DEFAULT_SIZE, java.lang.Short.MAX_VALUE.toInt()))
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(jTextFieldAlfabeto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextFieldCadena, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2).addComponent(jButtonProbarAutomata))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addComponent(jLabel3)
                                .addComponent(graphComponent, 500, javax.swing.GroupLayout.DEFAULT_SIZE, java.lang.Short.MAX_VALUE.toInt())))
    }

    private fun nombrarTransicion(): Char {
        //throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings |
        var nombre: Char = 0.toChar()
        while (true) {
            val name = JOptionPane.showInputDialog("Nombre de transicion:")
            if (name == null || name.isEmpty()) {
                ConfigurationForWindows.messageDialog(contentPane,"Cancelado")
                break
            }
            val na = name.toCharArray()
            if (na.size > 1) {
                ConfigurationForWindows.messageDialog(contentPane,"Solo un caracter es permitido")
            } else if (na.size == 1) {
                nombre = na[0]
                break
            }
        }
        return nombre
    }

    private fun obtenerEstado(origen: mxCell): Estado? {
        //throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
        for (estado in dfa?.estados!!) {
            if (estado.vertice?.equals(origen)!!) {
                return estado
            }
        }
        return null
    }

    private fun evaluarCadena(e: ActionEvent) {
        //throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
        if(prologoAntesDeProbar())
            return;
        if(!((seCreaAlfabeto(jTextFieldAlfabeto?.getText()?.toCharArray() as CharArray)) as Boolean))
            return;

        if(dfa?.evaluar(jTextFieldCadena?.text.toString()) as Boolean){
            JOptionPane.showMessageDialog(getContentPane(),"EL AUTOMATA ACEPTA LA CADENA!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        ConfigurationForWindows.messageDialog(contentPane,"EL AUTOMATA NO ACEPTA LA CADENA");
    }

    private fun seCreaAlfabeto(alfabeto: CharArray): Any {
           // throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
        for (i in 0..alfabeto.size - 1) {
            for (j in i + 1..alfabeto.size - 1) {
                if (alfabeto[j] === alfabeto[i]) {
                    ConfigurationForWindows.messageDialog(contentPane,"No es necesario agregar dos veces el mismo digito o letra!")
                    return false
                }
            }
        }

       /* if (!(dfa?.alfabeto?.isEmpty() as Boolean)) {
            dfa?.alfabeto = ArrayList()
        }*/
        for (i in 0..alfabeto.size - 1) {
            dfa?.alfabeto?.add( alfabeto[i].toChar())
        }
        return true
    }

    private fun prologoAntesDeProbar(): Boolean {
        //throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
        if (dfa?.estados?.isEmpty() as Boolean) {
            ConfigurationForWindows.messageDialog(contentPane,"No hay estado!");
            return true;
        }
        if (dfa?.estadoInicial?.nombre?.isEmpty() as Boolean) {
            ConfigurationForWindows.messageDialog(contentPane,"Debe existir un estado inicial!");
            return true;
        }
        if(dfa?.estadosDeAceptacion?.isEmpty() as Boolean){
            ConfigurationForWindows.messageDialog(contentPane,"Estado de aceptacion Oblogatorio");
            return true;
        }
        if (jTextFieldAlfabeto?.getText()?.isEmpty() as Boolean) {
            ConfigurationForWindows.messageDialog(contentPane,"No hay alfabeto!");
            return true;
        }
        return false;
    }
}


fun main(args : Array<String>) {
    val automataDFA = AutomataDFA()

    var dfa = DFA(automataDFA)

    automataDFA.toString()

    dfa.initComponents()
    dfa.setVisible(true)
}