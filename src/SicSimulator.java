import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * �ùķ����ͷμ��� �۾��� ����Ѵ�. VisualSimulator���� ������� ��û�� ������ �̿� ����
 * ResourceManager�� �����Ͽ� �۾��� �����Ѵ�.
 * <p>
 * �ۼ����� ���ǻ��� : <br>
 * 1) ���ο� Ŭ����, ���ο� ����, ���ο� �Լ� ������ �󸶵��� ����. ��, ������ ������ �Լ����� �����ϰų� ������ ��ü�ϴ� ���� ������ ��.<br>
 * 2) �ʿ信 ���� ����ó��, �������̽� �Ǵ� ��� ��� ���� ����.<br>
 * 3) ��� void Ÿ���� ���ϰ��� ������ �ʿ信 ���� �ٸ� ���� Ÿ������ ���� ����.<br>
 * 4) ����, �Ǵ� �ܼ�â�� �ѱ��� ��½�Ű�� �� ��. (ä������ ����. �ּ��� ���Ե� �ѱ��� ��� ����)<br>
 *
 * <br><br>
 * + �����ϴ� ���α׷� ������ ��������� �����ϰ� ���� �е��� ������ ��� �޺κп� ÷�� �ٶ��ϴ�. ���뿡 ���� �������� ���� �� �ֽ��ϴ�.
 */
public class SicSimulator {
    ResourceManager rMgr;
    InstLuncher instLuncher;
    private ArrayList<String> log;
    private ArrayList<String> instructions;
    private String codes;
    private int codeCur;

    public SicSimulator(ResourceManager resourceManager) {
        this.rMgr = resourceManager;
        this.instLuncher = new InstLuncher(resourceManager);
        this.log = new ArrayList<String>();
        this.instructions = new ArrayList<String>();
    }

    /**
     * ��������, �޸� �ʱ�ȭ �� ���α׷� load�� ���õ� �۾� ����.
     * ��, object code�� �޸� ���� �� �ؼ��� SicLoader���� �����ϵ��� �Ѵ�.
     */
    public void load(File objectCode) {
        /* �޸� �ʱ�ȭ, �������� �ʱ�ȭ ��*/
        for (int i = 0; i < 10; i++)
            rMgr.register[i] = 0;
        rMgr.register_F = 0;
        this.codes = "";
        this.codeCur = 0;
        String line = "";
        BufferedReader bufReader;
        ArrayList<SicLoader.Modify> mRec = new ArrayList<SicLoader.Modify>();
        try {
            bufReader = new BufferedReader(new FileReader(objectCode));
            while ((line = bufReader.readLine()) != null)
                if (line.length() != 0 && line.substring(0, 1).equals("T"))
                    codes += line.substring(9, line.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1���� instruction�� ����� ����� ���δ�.
     */
    public Boolean oneStep() {
        int format = -1;
        String code, inst = "";
        byte opcode = (byte) (rMgr.memory[rMgr.register[8]] & ~3);
        format = instLuncher.getInstFormat(opcode);
        inst = instLuncher.getInst(opcode);
        if (format == 1) {
            code = String.format("%02X", rMgr.memory[rMgr.register[8]++]);
            if (!codes.substring(codeCur, codeCur + 2).equals(code))
                return false;
            codeCur += 2;
            instLuncher.executeInst(opcode, null, 1, null);
        } else if (format == 2) {
            code = String.format("%02X%02X", rMgr.memory[rMgr.register[8]++], rMgr.memory[rMgr.register[8]++]);
            instLuncher.executeInst(opcode, code.substring(2, 4), 2, null);
            codeCur += 4;
        } else {
            byte ni = 3;
            int addressingMode = ni & rMgr.memory[rMgr.register[8]];
            if ((rMgr.memory[rMgr.register[8] + 1] & 16) != 16) {
                code = String.format("%02X%02X%02X", rMgr.memory[rMgr.register[8]++], rMgr.memory[rMgr.register[8]++], rMgr.memory[rMgr.register[8]++]);
                instLuncher.executeInst(opcode, code.substring(3, 6), 0, addressingMode);
                codeCur += 6;
            } else {
                code = String.format("%02X%02X%02X%02X", rMgr.memory[rMgr.register[8]++], rMgr.memory[rMgr.register[8]++], rMgr.memory[rMgr.register[8]++], rMgr.memory[rMgr.register[8]++]);
                instLuncher.executeInst(opcode, code.substring(3, 8), 0, addressingMode);
                codeCur += 8;
            }
        }
        addLog(inst, code);
        return true;
    }

    /**
     * ���� ��� instruction�� ����� ����� ���δ�.
     */
    public void allStep() {
        oneStep();
        while (!(rMgr.getRegister(8) == rMgr.startAddr)) {
            oneStep();
        }
    }

    /**
     * �� �ܰ踦 ������ �� ���� ���õ� ����� ���⵵�� �Ѵ�.
     */
    public void addLog(String inst, String code) {
        this.log.add(inst + "\n");
        this.instructions.add(code);
    }

    public ArrayList<String> getLog() {
        return this.log;
    }

    public ArrayList<String> getInstructions() {
        return this.instructions;
    }
}
