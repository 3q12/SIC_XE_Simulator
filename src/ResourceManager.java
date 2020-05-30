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
    byte[] memory = new byte[131072]; // String���� �����ؼ� ����Ͽ��� ������.
    int[] register = new int[10];
    double register_F;

    SymbolTable symtabList;
    String programName;
    int startAddr;
    int programLength;
    int memCur;
    int finAddr;
    String usingDevice;
    File device;
    byte[] deviceStream = new byte[4096];
    int deviceCur;
    FileWriter fw;
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
    }

    /**
     * deviceManager�� �����ϰ� �ִ� ���� ����� stream���� ���� �����Ű�� ����.
     * ���α׷��� �����ϰų� ������ ���� �� ȣ���Ѵ�.
     */
    public void closeDevice() {

    }

    /**
     * ����̽��� ����� �� �ִ� ��Ȳ���� üũ. TD��ɾ ������� �� ȣ��Ǵ� �Լ�.
     * ����� stream�� ���� deviceManager�� ���� ������Ų��.
     *
     * @param devName Ȯ���ϰ��� �ϴ� ����̽��� ��ȣ,�Ǵ� �̸�
     */
    public void testDevice(String devName) {
        register[9]=0;
        if(!devName.equals(usingDevice)){
            try {
                if(device != null){
                    fw.close();
                }
                usingDevice = devName;
                device = new File(devName);
                FileInputStream deviceInputStream = new FileInputStream(device);
                deviceInputStream.read(deviceStream);
                deviceInputStream.close();
                fw = new FileWriter(device);
                fw.write("");
                fw.flush();
                deviceCur=0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        register[9]=-1;
    }

    /**
     * ����̽��κ��� ���ϴ� ������ŭ�� ���ڸ� �о���δ�. RD��ɾ ������� �� ȣ��Ǵ� �Լ�.
     *
     * @param devName ����̽��� �̸�
     * @param num     �������� ������ ����
     * @return ������ ������
     */
    public byte readDevice(String devName, int num) {
        System.out.println("reading device" + deviceStream[deviceCur]);
        if(deviceCur<deviceStream.toString().length())
            return deviceStream[deviceCur++];
        else
            return 0;
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
        return 0;

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
        String dataString = String.format("%06X",data);
        byte[] ret = new byte[3];
        ret[0]= (byte) Integer.parseInt(dataString.substring(0,2));
        ret[1]= (byte) Integer.parseInt(dataString.substring(2,4));
        ret[2]= (byte) Integer.parseInt(dataString.substring(4,6));
        return ret;
    }

    /**
     * �ַ� �������Ϳ� �޸𸮰��� ������ ��ȯ���� ���ȴ�. char[]���� int���·� �����Ѵ�.
     *
     * @param data
     * @return
     */
    public int byteToInt(byte[] data) {
        return 0;
    }
}