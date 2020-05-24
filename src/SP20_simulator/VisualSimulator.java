package SP20_simulator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class VisualSimulator extends JFrame {

    private JPanel contentPane;
    ResourceManager resourceManager = new ResourceManager();
    SicLoader sicLoader = new SicLoader(resourceManager);
    SicSimulator sicSimulator = new SicSimulator(resourceManager);
    private JTextField FilePath;

    /**
     * 프로그램 로드 명령을 전달한다.
     */
    public void load(File program) {
        sicLoader.load(program);
        sicSimulator.load(program);
    }

    ;

    /**
     * 하나의 명령어만 수행할 것을 SicSimulator에 요청한다.
     */
    public void oneStep() {
        update();
    }

    ;

    /**
     * 남아있는 모든 명령어를 수행할 것을 SicSimulator에 요청한다.
     */
    public void allStep() {
        update();
    }

    ;

    /**
     * 화면을 최신값으로 갱신하는 역할을 수행한다.
     */
    public void update() {

    }

    ;

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
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JButton openFileBtn = new JButton("open");
        openFileBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        	    Frame f = new Frame("Parent");
    			// 1. FileDialog를 열어 불러올 파일 지정
    			FileDialog dialog = new FileDialog(f, "열기", FileDialog.LOAD);
    			dialog.setDirectory(".");
    			dialog.setVisible(true);
    			// 2. FileDialog가 비정상 종료되었을때

    			if(dialog.getFile() == null) return;

    			// 3. 파일 열기, TextArea에 뿌려주기

    			try {
                    String dfName = dialog.getDirectory() + dialog.getFile();
                    File program = new File(dfName);
                    load(program);
    			} catch (Exception e2) {
    				JOptionPane.showMessageDialog(f, "열기 오류");
    			}
                // 4. 파일명 표시하기
                FilePath.setText(dialog.getFile());
                setTitle(dialog.getFile());
    		}
        });
        openFileBtn.setBounds(206, 17, 68, 23);
        contentPane.add(openFileBtn);

        FilePath = new JTextField();
        FilePath.setEditable(false);
        FilePath.setBounds(78, 17, 116, 23);
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
        StartAddressLabel.setBounds(16, 63, 92, 15);
        HRec.add(StartAddressLabel);

        JLabel ProgramLengthLabel = new JLabel("Length of Program: ");
        ProgramLengthLabel.setBounds(16, 88, 112, 15);
        HRec.add(ProgramLengthLabel);

        JLabel StartAddressLabel0 = new JLabel("Start Address of");
        StartAddressLabel0.setBounds(16, 47, 92, 15);
        HRec.add(StartAddressLabel0);

        JTextPane programName = new JTextPane();
        programName.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        programName.setEditable(false);
        programName.setBackground(UIManager.getColor("CheckBox.background"));
        programName.setBounds(120, 22, 83, 18);
        HRec.add(programName);

        JTextPane objStartAddr = new JTextPane();
        objStartAddr.setEditable(false);
        objStartAddr.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        objStartAddr.setBackground(SystemColor.menu);
        objStartAddr.setBounds(120, 60, 83, 18);
        HRec.add(objStartAddr);

        JTextPane programLen = new JTextPane();
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

        JTextPane firstInstAddr = new JTextPane();
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

        JTextPane startAddrInMem = new JTextPane();
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

        JTextPane aRegHex = new JTextPane();
        aRegHex.setEditable(false);
        aRegHex.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        aRegHex.setBackground(SystemColor.menu);
        aRegHex.setBounds(134, 40, 65, 18);
        Register.add(aRegHex);

        JTextPane aRegDec = new JTextPane();
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

        JTextPane xRegDec = new JTextPane();
        xRegDec.setEditable(false);
        xRegDec.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        xRegDec.setBackground(SystemColor.menu);
        xRegDec.setBounds(57, 61, 65, 18);
        Register.add(xRegDec);

        JTextPane xRegHex = new JTextPane();
        xRegHex.setEditable(false);
        xRegHex.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        xRegHex.setBackground(SystemColor.menu);
        xRegHex.setBounds(134, 61, 65, 18);
        Register.add(xRegHex);

        JLabel LLabel = new JLabel("L(#2)");
        LLabel.setBounds(12, 84, 37, 15);
        Register.add(LLabel);

        JTextPane lRegDec = new JTextPane();
        lRegDec.setEditable(false);
        lRegDec.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        lRegDec.setBackground(SystemColor.menu);
        lRegDec.setBounds(57, 83, 65, 18);
        Register.add(lRegDec);

        JTextPane lRegHex = new JTextPane();
        lRegHex.setEditable(false);
        lRegHex.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        lRegHex.setBackground(SystemColor.menu);
        lRegHex.setBounds(134, 83, 65, 18);
        Register.add(lRegHex);

        JLabel BLabel = new JLabel("B(#3)");
        BLabel.setBounds(12, 106, 37, 15);
        Register.add(BLabel);

        JTextPane bRegDec = new JTextPane();
        bRegDec.setEditable(false);
        bRegDec.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        bRegDec.setBackground(SystemColor.menu);
        bRegDec.setBounds(57, 105, 65, 18);
        Register.add(bRegDec);

        JTextPane bRegHex = new JTextPane();
        bRegHex.setEditable(false);
        bRegHex.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        bRegHex.setBackground(SystemColor.menu);
        bRegHex.setBounds(134, 105, 65, 18);
        Register.add(bRegHex);

        JLabel SLabel = new JLabel("S(#4)");
        SLabel.setBounds(12, 129, 37, 15);
        Register.add(SLabel);

        JTextPane sRegDec = new JTextPane();
        sRegDec.setEditable(false);
        sRegDec.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        sRegDec.setBackground(SystemColor.menu);
        sRegDec.setBounds(57, 128, 65, 18);
        Register.add(sRegDec);

        JTextPane sRegHex = new JTextPane();
        sRegHex.setEditable(false);
        sRegHex.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        sRegHex.setBackground(SystemColor.menu);
        sRegHex.setBounds(134, 128, 65, 18);
        Register.add(sRegHex);

        JLabel TLabel = new JLabel("T(#5)");
        TLabel.setBounds(12, 150, 37, 15);
        Register.add(TLabel);

        JTextPane tRegDec = new JTextPane();
        tRegDec.setEditable(false);
        tRegDec.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        tRegDec.setBackground(SystemColor.menu);
        tRegDec.setBounds(57, 149, 65, 18);
        Register.add(tRegDec);

        JTextPane tRegHex = new JTextPane();
        tRegHex.setEditable(false);
        tRegHex.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        tRegHex.setBackground(SystemColor.menu);
        tRegHex.setBounds(134, 149, 65, 18);
        Register.add(tRegHex);

        JLabel FLabel = new JLabel("F(#6)");
        FLabel.setBounds(12, 173, 37, 15);
        Register.add(FLabel);

        JTextPane fReg = new JTextPane();
        fReg.setEditable(false);
        fReg.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        fReg.setBackground(SystemColor.menu);
        fReg.setBounds(57, 172, 142, 18);
        Register.add(fReg);

        JLabel PCLabel = new JLabel("PC(#8)");
        PCLabel.setBounds(12, 195, 40, 15);
        Register.add(PCLabel);

        JTextPane pcRegDec = new JTextPane();
        pcRegDec.setEditable(false);
        pcRegDec.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        pcRegDec.setBackground(SystemColor.menu);
        pcRegDec.setBounds(57, 194, 65, 18);
        Register.add(pcRegDec);

        JTextPane pcRegHex = new JTextPane();
        pcRegHex.setEditable(false);
        pcRegHex.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        pcRegHex.setBackground(SystemColor.menu);
        pcRegHex.setBounds(134, 194, 65, 18);
        Register.add(pcRegHex);

        JLabel SWLabel = new JLabel("SW(#9)");
        SWLabel.setBounds(12, 217, 40, 15);
        Register.add(SWLabel);

        JTextPane swReg = new JTextPane();
        swReg.setEditable(false);
        swReg.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        swReg.setBackground(SystemColor.menu);
        swReg.setBounds(57, 216, 142, 18);
        Register.add(swReg);

        JLabel LogLabel = new JLabel("Log (명령어 수행 관련):");
        LogLabel.setBounds(12, 437, 136, 15);
        contentPane.add(LogLabel);

        JTextArea logs = new JTextArea();
        logs.setBounds(12, 462, 446, 137);
        contentPane.add(logs);

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

        List instructions = new List();
        instructions.setBounds(239, 227, 110, 198);
        contentPane.add(instructions);

        JButton run1StepBtn = new JButton("실행(1step)");
        run1StepBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	    oneStep();
        	}
        });
        run1StepBtn.setBounds(354, 334, 100, 23);
        contentPane.add(run1StepBtn);

        JButton runAllBtn = new JButton("실행(all)");
        runAllBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	    allStep();
        	}
        });
        runAllBtn.setBounds(354, 362, 100, 23);
        contentPane.add(runAllBtn);

        JButton closeBtn = new JButton("종료");
        closeBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	    System.exit(0);
        	}
        });
        closeBtn.setBounds(354, 390, 100, 23);
        contentPane.add(closeBtn);

        JLabel DeviceLabel = new JLabel("사용중인 장치");
        DeviceLabel.setBounds(365, 227, 82, 15);
        contentPane.add(DeviceLabel);

        JTextPane runningDevice = new JTextPane();
        runningDevice.setEditable(false);
        runningDevice.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
        runningDevice.setBackground(SystemColor.menu);
        runningDevice.setBounds(370, 250, 65, 18);
        contentPane.add(runningDevice);
    }
}
