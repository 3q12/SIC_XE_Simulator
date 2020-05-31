import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * ResourceManager�� ��ǻ���� ���� ���ҽ����� �����ϰ� �����ϴ� Ŭ�����̴�.
 * ũ�� �װ����� ���� �ڿ� ������ �����ϰ�, �̸� ������ �� �ִ� �Լ����� �����Ѵ�.<br><br>
 * <p>
 * 1) ������� ���� �ܺ� ��ġ �Ǵ� device<br>
 * 2) ���α׷� �ε� �� ������ ���� �޸� ����. ���⼭�� 64KB�� �ִ밪���� ��´�.<br>
 * 3) ������ �����ϴµ� ����ϴ� �������� ����.<br>
 * 4) SYMTAB �� simulator�� ���� �������� ���Ǵ� �����͵��� ���� ������.
 * <br><br>
 * 2���� simulator������ ����Ǵ� ���α׷��� ���� �޸𸮰����� �ݸ�,
 * 4���� simulator�� ������ ���� �޸� �����̶�� ������ ���̰� �ִ�.
 */
public class ResourceManager {
    /**
     * ����̽��� ���� ����� ��ġ���� �ǹ� ������ ���⼭�� ���Ϸ� ����̽��� ��ü�Ѵ�.<br>
     * ��, 'F1'�̶�� ����̽��� 'F1'�̶�� �̸��� ������ �ǹ��Ѵ�. <br>
     * deviceManager�� ����̽��� �̸��� �Է¹޾��� �� �ش� �̸��� ���� ����� ���� Ŭ������ �����ϴ� ������ �Ѵ�.
     * ���� ���, 'A1'�̶�� ����̽����� ������ read���� ������ ���, hashMap�� <"A1", scanner(A1)> ���� �������μ� �̸� ������ �� �ִ�.
     * <br><br>
     * ������ ���·� ����ϴ� �� ���� ����Ѵ�.<br>
     * ���� ��� key������ String��� Integer�� ����� �� �ִ�.
     * ���� ������� ���� ����ϴ� stream ���� �������� ����, �����Ѵ�.
     * <br><br>
     * �̰͵� �����ϸ� �˾Ƽ� �����ؼ� ����ص� �������ϴ�.
     */
    HashMap<String, Object> deviceManager = new HashMap<String, Object>();
    byte[] memory = new byte[64000];
    int[] register = new int[10];
    double register_F;

    SymbolTable symtabList;
    String programName;
    int startAddr;
    int programLength;
    int memCur;
    int finAddr;
    String usingDevice;
    int changedMemAddr, changedMemSize;
    int instAddr = 0;
    // �̿ܿ��� �ʿ��� ���� �����ؼ� ����� ��.

    /**
     * �޸�, �������͵� ���� ���ҽ����� �ʱ�ȭ�Ѵ�.
     */
    public void initializeResource() {
        symtabList = new SymbolTable();
        programName = "";
        startAddr = 0;
        programLength = 0;
        memCur = 0;
        usingDevice = "";
        changedMemAddr = 0;
        changedMemSize = 0;
        File F1 = new File("F1");
        File Five = new File("05");
        try {
            deviceManager.put("F1", new FileInputStream(F1));
            deviceManager.put("05", new FileWriter(Five));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * deviceManager�� �����ϰ� �ִ� ���� ����� stream���� ���� �����Ű�� ����.
     * ���α׷��� �����ϰų� ������ ���� �� ȣ���Ѵ�.
     */
    public void closeDevice() {
        FileInputStream fis = (FileInputStream) deviceManager.get("F1");
        FileWriter fw = (FileWriter) deviceManager.get("05");
        try {
            fis.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ����̽��� ����� �� �ִ� ��Ȳ���� üũ. TD��ɾ ������� �� ȣ��Ǵ� �Լ�.
     * ����� stream�� ���� deviceManager�� ���� ������Ų��.
     *
     * @param devName Ȯ���ϰ��� �ϴ� ����̽��� ��ȣ,�Ǵ� �̸�
     */
    public void testDevice(String devName) {
        usingDevice = devName;
        if (deviceManager.get(devName) == null)
            register[9] = 0;
        else
            register[9] = -1;
    }

    /**
     * ����̽��κ��� ���ϴ� ������ŭ�� ���ڸ� �о���δ�. RD��ɾ ������� �� ȣ��Ǵ� �Լ�.
     *
     * @param devName ����̽��� �̸�
     * @param num     �������� ������ ����
     * @return ������ ������
     */
    public byte readDevice(String devName, int num) {
        int readByte = 0;
        FileInputStream fis = (FileInputStream) deviceManager.get(devName);
        try {
            readByte = fis.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (readByte == -1)
            return 0;
        return (byte) readByte;
    }

    /**
     * ����̽��� ���ϴ� ���� ��ŭ�� ���ڸ� ����Ѵ�. WD��ɾ ������� �� ȣ��Ǵ� �Լ�.
     *
     * @param devName ����̽��� �̸�
     * @param data    ������ ������
     * @param num     ������ ������ ����
     */
    public void writeDevice(String devName, char[] data, int num) {
        try {
            FileWriter fw = (FileWriter) deviceManager.get(devName);
            fw.write(data);
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * �޸��� Ư�� ��ġ���� ���ϴ� ������ŭ�� ���ڸ� �����´�.
     *
     * @param location �޸� ���� ��ġ �ε���
     * @param num      ������ ����
     * @return �������� ������
     */
    public byte[] getMemory(int location, int num) {
        byte[] retValue = new byte[num];
        for (int i = 0; i < num; i++)
            retValue[i] = this.memory[location + i];
        return retValue;
    }

    /**
     * �޸��� Ư�� ��ġ�� ���ϴ� ������ŭ�� �����͸� �����Ѵ�.
     *
     * @param locate ���� ��ġ �ε���
     * @param data   �����Ϸ��� ������
     * @param num    �����ϴ� �������� ����
     */
    public void setMemory(int locate, byte[] data, int num) {
        this.changedMemAddr = locate;
        this.changedMemSize = num * 2;
        for (int i = 0; i < num; i++)
            this.memory[locate + i] = data[i];
    }

    /**
     * ��ȣ�� �ش��ϴ� �������Ͱ� ���� ��� �ִ� ���� �����Ѵ�. �������Ͱ� ��� �ִ� ���� ���ڿ��� �ƴԿ� �����Ѵ�.
     *
     * @param regNum �������� �з���ȣ
     * @return �������Ͱ� ������ ��
     */
    public int getRegister(int regNum) {
        return register[regNum];

    }

    /**
     * ��ȣ�� �ش��ϴ� �������Ϳ� ���ο� ���� �Է��Ѵ�. �������Ͱ� ��� �ִ� ���� ���ڿ��� �ƴԿ� �����Ѵ�.
     *
     * @param regNum ���������� �з���ȣ
     * @param value  �������Ϳ� ����ִ� ��
     */
    public void setRegister(int regNum, int value) {
        register[regNum] = value;
    }

    /**
     * �ַ� �������Ϳ� �޸𸮰��� ������ ��ȯ���� ���ȴ�. int���� char[]���·� �����Ѵ�.
     *
     * @param data
     * @return
     */
    public byte[] intToByte(int data) {
        String dataString = String.format("%06X", data);
        byte[] ret = new byte[3];
        ret[0] = (byte) Integer.parseInt(dataString.substring(0, 2), 16);
        ret[1] = (byte) Integer.parseInt(dataString.substring(2, 4), 16);
        ret[2] = (byte) Integer.parseInt(dataString.substring(4, 6), 16);
        return ret;
    }

    /**
     * �ַ� �������Ϳ� �޸𸮰��� ������ ��ȯ���� ���ȴ�. char[]���� int���·� �����Ѵ�.
     *
     * @param data
     * @return
     */
    public int byteToInt(byte[] data) {
        return Integer.parseInt(String.format("%02X%02X%02X", data[0], data[1], data[2]), 16);
    }
}