import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.io.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class VisualSimulator extends JFrame {

    ResourceManager resourceManager = new ResourceManager();
    SicLoader sicLoader = new SicLoader(resourceManager);
    private JTextArea logs;
    private List instructions;
    JTextArea memory;
    SicSimulator sicSimulator = new SicSimulator(resourceManager);
    private final JTextPane programName;
    private final JTextPane objStartAddr;
    private final JTextPane programLen;
    private final JTextPane firstInstAddr;
    private final JTextPane startAddrInMem;
    private final JTextPane aRegHex;
    private final JTextPane aRegDec;
    private final JTextPane xRegHex;
    private final JTextPane xRegDec;
    private final JTextPane lRegHex;
    private final JTextPane lRegDec;
    private final JTextPane bRegHex;
    private final JTextPane bRegDec;
    private final JTextPane sRegHex;
    private final JTextPane sRegDec;
    private final JTextPane tRegHex;
    private final JTextPane tRegDec;
    private final JTextPane fReg;
    private final JTextPane pcRegHex;
    private final JTextPane pcRegDec;
    private final JTextPane swReg;
    private final JButton run1StepBtn;
    private final JButton runAllBtn;
    private final JTextPane runningDevice;

    /**
     * ���α׷� �ε� ����� �����Ѵ�.
     */
    public void load(File program) {
        resourceManager.initializeResource();
        sicLoader.load(program);
        sicSimulator.load(program);
        programName.setText(resourceManager.programName);
        objStartAddr.setText(String.format("%06X", resourceManager.startAddr));
        programLen.setText(String.format("%X", resourceManager.programLength));
        firstInstAddr.setText("000000");
        startAddrInMem.setText("0");
        runningDevice.setText("");
        memory.selectAll();
        memory.replaceSelection("");
        resourceManager.changedMemSize = -1;
        resourceManager.changedMemAddr = -1;
        update();
        instructions.removeAll();
        logs.selectAll();
        logs.replaceSelection("");
        run1StepBtn.setEnabled(true);
        runAllBtn.setEnabled(true);
    }

    /**
     * �ϳ��� ��ɾ ������ ���� SicSimulator�� ��û�Ѵ�.
     */
    public void oneStep() {
        while (!sicSimulator.oneStep()) ;
        update();
    }

    /**
     * �����ִ� ��� ��ɾ ������ ���� SicSimulator�� ��û�Ѵ�.
     */
    public void allStep() {
        sicSimulator.allStep();
        update();
    }

    /**
     * ȭ���� �ֽŰ����� �����ϴ� ������ �����Ѵ�.
     */
    public void update() {
        this.updateMemoryBox();
        this.updateRegisterBox();
        runningDevice.setText(resourceManager.usingDevice);
        this.updateLogBox();
        this.updateMemoryBox();
        // ����� �޸� ���̶�����
        this.highlightMemory();
        this.updateInstructionBox();
    }

    public void updateInstructionBox() {
        if (sicSimulator.getInstructions().size() == 1) {
            instructions.add(sicSimulator.getInstructions().get(0));
            if (resourceManager.getRegister(8) == resourceManager.startAddr) {
                resourceManager.closeDevice();
                run1StepBtn.setEnabled(false);
                runAllBtn.setEnabled(false);
            }
        } else {
            for (int i = 0; i < sicSimulator.getInstructions().size(); i++)
                instructions.add(sicSimulator.getInstructions().get(i));
            run1StepBtn.setEnabled(false);
            runAllBtn.setEnabled(false);
        }
        sicSimulator.getInstructions().clear();
        instructions.select(instructions.getItemCount() - 1);
    }

    public void updateLogBox() {
        if (sicSimulator.getLog().size() == 1)
            logs.append(sicSimulator.getLog().get(0));
        else
            for (int i = 0; i < sicSimulator.getLog().size(); i++)
                logs.append(sicSimulator.getLog().get(i));
        sicSimulator.getLog().clear();
    }

    public void updateMemoryBox() {
        memory.selectAll();
        memory.replaceSelection("");
        for (int i = 0; i < resourceManager.memCur; i += 16) {
            byte[] mem = resourceManager.getMemory(i, 16);
            memory.append(String.format("%06X  %02X%02X%02X%02X  %02X%02X%02X%02X  %02X%02X%02X%02X  %02X%02X%02X%02X\n",
                    i, mem[0], mem[1], mem[2], mem[3],
                    mem[4], mem[5], mem[6], mem[7],
                    mem[8], mem[9], mem[10], mem[11],
                    mem[12], mem[13], mem[14], mem[15]));
        }
    }

    public void highlightMemory() {
        if (resourceManager.changedMemAddr != -1) {
            int addr = (resourceManager.changedMemAddr / 16) * 47
                    + 8 + 10 * ((resourceManager.changedMemAddr % 16) / 4)
                    + ((resourceManager.changedMemAddr % 16) % 4) * 2;
            memory.getHighlighter().removeAllHighlights();
            try {
                for (int i = 0; i < resourceManager.changedMemSize; i += 2) {
                    if ((addr % 47 + i) % 10 == 6) {
                        resourceManager.changedMemSize += 2;
                        i += 2;
                    }
                    memory.getHighlighter().addHighlight(addr + i, addr + i + 2,
                            new DefaultHighlighter.DefaultHighlightPainter(Color.CYAN));
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
            memory.select(addr, addr + resourceManager.changedMemSize);
            resourceManager.changedMemAddr = -1;
            resourceManager.changedMemSize = -1;
        } else
            memory.select(0, 0);
    }

    public void updateRegisterBox() {
        aRegHex.setText(String.format("%06X", resourceManager.register[0]));
        aRegDec.setText(String.format("%d", resourceManager.register[0]));
        xRegHex.setText(String.format("%06X", resourceManager.register[1]));
        xRegDec.setText(String.format("%d", resourceManager.register[1]));
        lRegHex.setText(String.format("%06X", resourceManager.register[2]));
        lRegDec.setText(String.format("%d", resourceManager.register[2]));
        bRegHex.setText(String.format("%06X", resourceManager.register[3]));
        bRegDec.setText(String.format("%d", resourceManager.register[3]));
        sRegHex.setText(String.format("%06X", resourceManager.register[4]));
        sRegDec.setText(String.format("%d", resourceManager.register[4]));
        tRegHex.setText(String.format("%06X", resourceManager.register[5]));
        tRegDec.setText(String.format("%d", resourceManager.register[5]));
        fReg.setText(String.format("%f", resourceManager.register_F));
        pcRegHex.setText(String.format("%06X", resourceManager.register[8]));
        pcRegDec.setText(String.format("%d", resourceManager.register[8]));
        swReg.setText(String.format("%06X", resourceManager.register[9]));
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    VisualSimulator frame = new VisualSimulator();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public VisualSimulator() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 486, 648);
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JTextField FilePath = new JTextField();
        JButton openFileBtn = new JButton("open");
        openFileBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                Frame f = new Frame("Parent");
                FileDialog dialog = new FileDialog(f, "����", FileDialog.LOAD);
                dialog.setDirectory(".");
                dialog.setVisible(true);
                if (dialog.getFile() == null) return;

                try {
                    String dfName = dialog.getDirectory() + dialog.getFile();
                    File program = new File(dfName);
                    load(program);
                } catch (Exception e2) {
                    JOptionPane.showMessageDialog(f, "���� ����" + e2.getMessage());
                }
                FilePath.setText(dialog.getFile());
                setTitle(dialog.getFile());
            }
        });
        openFileBtn.setBounds(210, 17, 68, 23);
        contentPane.add(openFileBtn);


        FilePath.setEditable(false);
        FilePath.setBounds(78, 17, 125, 23);
        contentPane.add(FilePath);
        FilePath.setColumns(10);

        JLabel FileNameLabel = new JLabel("FileName:");
        FileNameLabel.setBounds(12, 21, 73, 15);
        contentPane.add(FileNameLabel);

        JPanel HRec = new JPanel();
        HRec.setBorder(new TitledBorder(null, "H (Header Record)", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        HRec.setToolTipText("");
        HRec.setBounds(12, 50, 215, 113);
        contentPane.add(HRec);
        HRec.setLayout(null);

        JLabel ProgramNameLabel = new JLabel("Program name: ");
        ProgramNameLabel.setBounds(16, 22, 92, 15);
        HRec.add(ProgramNameLabel);

        JLabel StartAddressLabel = new JLabel("Object Program:");
        StartAddressLabel.setBounds(16, 63, 95, 15);
        HRec.add(StartAddressLabel);

        JLabel ProgramLengthLabel = new JLabel("Length of Program: ");
        ProgramLengthLabel.setBounds(16, 88, 112, 15);
        HRec.add(ProgramLengthLabel);

        JLabel StartAddressLabel0 = new JLabel("Start Address of");
        StartAddressLabel0.setBounds(16, 47, 95, 15);
        HRec.add(StartAddressLabel0);

        programName = new JTextPane();
        programName.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        programName.setEditable(false);
        programName.setBackground(UIManager.getColor("CheckBox.background"));
        programName.setBounds(120, 22, 83, 18);
        HRec.add(programName);

        objStartAddr = new JTextPane();
        objStartAddr.setEditable(false);
        objStartAddr.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        objStartAddr.setBackground(SystemColor.menu);
        objStartAddr.setBounds(120, 60, 83, 18);
        HRec.add(objStartAddr);

        programLen = new JTextPane();
        programLen.setEditable(false);
        programLen.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        programLen.setBackground(SystemColor.menu);
        programLen.setBounds(130, 86, 73, 18);
        HRec.add(programLen);

        JPanel ERec = new JPanel();
        ERec.setToolTipText("");
        ERec.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "E (End Record)", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        ERec.setBounds(239, 50, 215, 60);
        contentPane.add(ERec);
        ERec.setLayout(null);

        JLabel FirstInstLabel1 = new JLabel("in Object Program: ");
        FirstInstLabel1.setBounds(16, 38, 110, 15);
        ERec.add(FirstInstLabel1);

        firstInstAddr = new JTextPane();
        firstInstAddr.setEditable(false);
        firstInstAddr.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        firstInstAddr.setBackground(SystemColor.menu);
        firstInstAddr.setBounds(129, 37, 74, 18);
        ERec.add(firstInstAddr);

        JLabel FirstInstLabel0 = new JLabel("Address of First instruction");
        FirstInstLabel0.setBounds(16, 22, 187, 15);
        ERec.add(FirstInstLabel0);

        JLabel StartAddrLabel = new JLabel("Start Address in Memory");
        StartAddrLabel.setBounds(239, 111, 150, 15);
        contentPane.add(StartAddrLabel);

        startAddrInMem = new JTextPane();
        startAddrInMem.setEditable(false);
        startAddrInMem.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        startAddrInMem.setBackground(SystemColor.menu);
        startAddrInMem.setBounds(354, 129, 100, 18);
        contentPane.add(startAddrInMem);

        JPanel Register = new JPanel();
        Register.setLayout(null);
        Register.setToolTipText("");
        Register.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Register", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        Register.setBounds(12, 181, 215, 246);
        contentPane.add(Register);

        JLabel ALabel = new JLabel("A(#0)");
        ALabel.setBounds(12, 41, 37, 15);
        Register.add(ALabel);

        aRegHex = new JTextPane();
        aRegHex.setEditable(false);
        aRegHex.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        aRegHex.setBackground(SystemColor.menu);
        aRegHex.setBounds(134, 40, 65, 18);
        Register.add(aRegHex);

        aRegDec = new JTextPane();
        aRegDec.setEditable(false);
        aRegDec.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        aRegDec.setBackground(SystemColor.menu);
        aRegDec.setBounds(57, 40, 65, 18);
        Register.add(aRegDec);

        JLabel DecLabel = new JLabel("Dec");
        DecLabel.setBounds(57, 20, 37, 15);
        Register.add(DecLabel);

        JLabel HexLabel = new JLabel("Hex");
        HexLabel.setBounds(134, 20, 37, 15);
        Register.add(HexLabel);

        JLabel XLabel = new JLabel("X(#1)");
        XLabel.setBounds(12, 62, 37, 15);
        Register.add(XLabel);

        xRegDec = new JTextPane();
        xRegDec.setEditable(false);
        xRegDec.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        xRegDec.setBackground(SystemColor.menu);
        xRegDec.setBounds(57, 61, 65, 18);
        Register.add(xRegDec);

        xRegHex = new JTextPane();
        xRegHex.setEditable(false);
        xRegHex.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        xRegHex.setBackground(SystemColor.menu);
        xRegHex.setBounds(134, 61, 65, 18);
        Register.add(xRegHex);

        JLabel LLabel = new JLabel("L(#2)");
        LLabel.setBounds(12, 84, 37, 15);
        Register.add(LLabel);

        lRegDec = new JTextPane();
        lRegDec.setEditable(false);
        lRegDec.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        lRegDec.setBackground(SystemColor.menu);
        lRegDec.setBounds(57, 83, 65, 18);
        Register.add(lRegDec);

        lRegHex = new JTextPane();
        lRegHex.setEditable(false);
        lRegHex.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        lRegHex.setBackground(SystemColor.menu);
        lRegHex.setBounds(134, 83, 65, 18);
        Register.add(lRegHex);

        JLabel BLabel = new JLabel("B(#3)");
        BLabel.setBounds(12, 106, 37, 15);
        Register.add(BLabel);

        bRegDec = new JTextPane();
        bRegDec.setEditable(false);
        bRegDec.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        bRegDec.setBackground(SystemColor.menu);
        bRegDec.setBounds(57, 105, 65, 18);
        Register.add(bRegDec);

        bRegHex = new JTextPane();
        bRegHex.setEditable(false);
        bRegHex.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        bRegHex.setBackground(SystemColor.menu);
        bRegHex.setBounds(134, 105, 65, 18);
        Register.add(bRegHex);

        JLabel SLabel = new JLabel("S(#4)");
        SLabel.setBounds(12, 129, 37, 15);
        Register.add(SLabel);

        sRegDec = new JTextPane();
        sRegDec.setEditable(false);
        sRegDec.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        sRegDec.setBackground(SystemColor.menu);
        sRegDec.setBounds(57, 128, 65, 18);
        Register.add(sRegDec);

        sRegHex = new JTextPane();
        sRegHex.setEditable(false);
        sRegHex.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        sRegHex.setBackground(SystemColor.menu);
        sRegHex.setBounds(134, 128, 65, 18);
        Register.add(sRegHex);

        JLabel TLabel = new JLabel("T(#5)");
        TLabel.setBounds(12, 150, 37, 15);
        Register.add(TLabel);

        tRegDec = new JTextPane();
        tRegDec.setEditable(false);
        tRegDec.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        tRegDec.setBackground(SystemColor.menu);
        tRegDec.setBounds(57, 149, 65, 18);
        Register.add(tRegDec);

        tRegHex = new JTextPane();
        tRegHex.setEditable(false);
        tRegHex.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        tRegHex.setBackground(SystemColor.menu);
        tRegHex.setBounds(134, 149, 65, 18);
        Register.add(tRegHex);

        JLabel FLabel = new JLabel("F(#6)");
        FLabel.setBounds(12, 173, 37, 15);
        Register.add(FLabel);

        fReg = new JTextPane();
        fReg.setEditable(false);
        fReg.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        fReg.setBackground(SystemColor.menu);
        fReg.setBounds(57, 172, 142, 18);
        Register.add(fReg);

        JLabel PCLabel = new JLabel("PC(#8)");
        PCLabel.setBounds(12, 195, 40, 15);
        Register.add(PCLabel);

        pcRegDec = new JTextPane();
        pcRegDec.setEditable(false);
        pcRegDec.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        pcRegDec.setBackground(SystemColor.menu);
        pcRegDec.setBounds(57, 194, 65, 18);
        Register.add(pcRegDec);

        pcRegHex = new JTextPane();
        pcRegHex.setEditable(false);
        pcRegHex.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        pcRegHex.setBackground(SystemColor.menu);
        pcRegHex.setBounds(134, 194, 65, 18);
        Register.add(pcRegHex);

        JLabel SWLabel = new JLabel("SW(#9)");
        SWLabel.setBounds(12, 217, 42, 15);
        Register.add(SWLabel);

        swReg = new JTextPane();
        swReg.setEditable(false);
        swReg.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        swReg.setBackground(SystemColor.menu);
        swReg.setBounds(57, 216, 142, 18);
        Register.add(swReg);

        JLabel LogLabel = new JLabel("Log (��ɾ� ���� ����):");
        LogLabel.setBounds(328, 437, 136, 15);
        contentPane.add(LogLabel);

        logs = new JTextArea();
        logs.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logs);
        scrollPane.setBounds(328, 462, 135, 137);
        scrollPane.setVisible(true);
        contentPane.add(scrollPane);

        JTextPane targetAddr = new JTextPane();
        targetAddr.setEditable(false);
        targetAddr.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        targetAddr.setBackground(SystemColor.menu);
        targetAddr.setBounds(354, 178, 100, 18);
        contentPane.add(targetAddr);

        JLabel TargetAddrLabel = new JLabel("Target Address:");
        TargetAddrLabel.setBounds(239, 181, 110, 15);
        contentPane.add(TargetAddrLabel);

        JLabel InstructionsLabel = new JLabel("Instructions:");
        InstructionsLabel.setBounds(239, 206, 73, 15);
        contentPane.add(InstructionsLabel);

        instructions = new List();
        instructions.setBounds(239, 227, 110, 198);
        contentPane.add(instructions);

        run1StepBtn = new JButton("����(1step)");
        run1StepBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                oneStep();
            }
        });
        run1StepBtn.setBounds(354, 334, 100, 23);
        contentPane.add(run1StepBtn);

        runAllBtn = new JButton("����(all)");
        runAllBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                allStep();
            }
        });
        runAllBtn.setBounds(354, 362, 100, 23);
        contentPane.add(runAllBtn);

        JButton closeBtn = new JButton("����");
        closeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        closeBtn.setBounds(354, 390, 100, 23);
        contentPane.add(closeBtn);

        JLabel DeviceLabel = new JLabel("������� ��ġ");
        DeviceLabel.setBounds(364, 227, 83, 15);
        contentPane.add(DeviceLabel);

        runningDevice = new JTextPane();
        runningDevice.setEditable(false);
        runningDevice.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        runningDevice.setBackground(SystemColor.menu);
        runningDevice.setBounds(370, 250, 65, 18);
        contentPane.add(runningDevice);

        JLabel MemoryLabel = new JLabel("Memory");
        MemoryLabel.setBounds(5, 438, 136, 15);
        contentPane.add(MemoryLabel);

        memory = new JTextArea();
        memory.setEditable(false);
        JScrollPane scrollPane_mem = new JScrollPane(memory);
        scrollPane_mem.setBounds(6, 464, 318, 135);
        scrollPane_mem.setVisible(true);
        contentPane.add(scrollPane_mem);
    }
}
