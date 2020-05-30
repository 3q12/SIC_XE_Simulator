// instruction에 따라 동작을 수행하는 메소드를 정의하는 클래스

import java.io.*;
import java.util.HashMap;

public class InstLuncher {
    ResourceManager rMgr;
    private HashMap<Byte, Instruction> instMap;
    private boolean XFlag;
    private boolean BFlag;
    private boolean PFlag;
    private boolean EFlag;

    public InstLuncher(ResourceManager resourceManager) {
        this.rMgr = resourceManager;
        this.instMap = new HashMap<Byte, Instruction>();
        try {
            File file = new File("./bin/inst.data");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufReader = new BufferedReader(fileReader);
            String line = "";
            while ((line = bufReader.readLine()) != null) {
                Instruction instruction = new Instruction(line);
                instMap.put(instruction.getOpcode(), instruction);
            }
        } catch (FileNotFoundException e) {
            System.out.println("[Error] inst.data not exist");
        } catch (IOException e) {
            System.out.println("[Error] IOError");
        }
    }

    public int getInstFormat(byte inst) {
        return instMap.get(inst).format;
    }

    public String getInst(byte inst) {
        return instMap.get(inst).instruction;
    }

    public void executeInst(byte opcode, String data, int format, Integer addressingMode) {
        String inst = instMap.get(opcode).instruction;
/*        System.out.println("inExecute = " + inst);
        System.out.println(data);*/
        if (format == 1) {

        }else{
            int flags = Integer.parseInt(data.substring(0, 1),16);
            if((flags & 8) == 8)
                XFlag=true;
            if((flags & 4) == 4)
                BFlag = true;
            else if((flags & 2) == 2)
                PFlag= true;
            else if((flags & 1) == 1)
                EFlag= true;
            if (format == 2) {
                if (inst.equals("CLEAR")) CLEAR(Integer.parseInt(data.substring(0, 1)));
                else if (inst.equals("COMPR")) COMPR(Integer.parseInt(data.substring(0, 1)),Integer.parseInt(data.substring(1, 2)));
                else if (inst.equals("TIXR")) TIXR(Integer.parseInt(data.substring(0, 1)));
            } else if (format == 0) {
                int originalData = Integer.parseInt(data, 16);
                int convertedData = 0;

                if (data.length() == 5)
                    addressingMode = 1;
                else if (originalData >= 3840)
                    originalData = -1 * (4096 - originalData);

                if (addressingMode == 1)
                    convertedData = originalData;
                else if (addressingMode == 2){
                    convertedData = rMgr.memory[originalData];/*
                System.out.println(convertedData);*/
                }
                else if (addressingMode == 3)
                    convertedData = originalData + rMgr.register[8];
            if(convertedData<0){
                convertedData = convertedData & 4095;
                if((convertedData & 3840) == 3840){
                    convertedData = convertedData & 255;
                }
            }

                if (inst.equals("STL")) STL(convertedData);
                else if (inst.equals("JSUB")) JSUB(convertedData);
                else if (inst.equals("RSUB")) RSUB();
                else if (inst.equals("J")) J(convertedData);
                else if (inst.equals("LDT")) LDT(convertedData);
                else if (inst.equals("LDA")) LDA(convertedData);
                else if (inst.equals("STX")) STX(convertedData);
                else if (inst.equals("TD")) TD(convertedData);
                else if (inst.equals("RD")) RD();
                else if (inst.equals("WD")) WD();
                else if (inst.equals("JEQ")) JEQ(convertedData);
                else if (inst.equals("JLT")) JLT(convertedData);
                else if (inst.equals("STCH")) STCH(convertedData, XFlag);
                else if (inst.equals("LDCH")) LDCH(convertedData, XFlag);
                else if (inst.equals("COMP")) COMP(convertedData);
                else if (inst.equals("STA")) STA(convertedData);
            }
        }
    }

    // Format 1

    //Format 2

    private void CLEAR(int reg) {
        rMgr.register[reg] = 0;
    }

    private void COMPR(int reg1, int reg2) {
        rMgr.register[9] = rMgr.register[reg1]-rMgr.register[reg2];
    }

    private void TIXR(int reg) {
        rMgr.register[9] = ++rMgr.register[1] - rMgr.register[reg];
    }

    //Format 3/4


    private void LDA(int data) {
        byte[] loadedData = rMgr.getMemory(data,3);
        rMgr.register[0] = Integer.parseInt(String.format("%02X%02X%02X",loadedData[0],loadedData[1],loadedData[2]),16);
    }

    private void LDT(int data) {
        byte[] loadedData = rMgr.getMemory(data,3);
        rMgr.register[5] = Integer.parseInt(String.format("%02X%02X%02X",loadedData[0],loadedData[1],loadedData[2]),16);
    }
    private void LDCH(int data ,Boolean useIndex) {
/*
        byte[] store = new byte[1];
        store[0]= (byte) rMgr.register[0];
        rMgr.setMemory(data+index,store,1);*/
        int index = 0;
        if(useIndex)
            index = rMgr.register[1];
        rMgr.setRegister(0,rMgr.getMemory(data + index,1)[0]);
    }
    private void STCH(int data ,Boolean useIndex) {
        int index = 0;
        if(useIndex)
            index = rMgr.register[1];
        byte[] store = new byte[1];
        store[0]= (byte) rMgr.register[0];
        rMgr.setMemory(data+index,store,1);
    }

    private void STX(int data) {
        byte[] bytedata = rMgr.intToByte(rMgr.register[1]);
        for(int i = 0; i<3; i++)
            rMgr.memory[data+i]= bytedata[i];
    }

    private void STA(int data) {
        rMgr.setMemory(data,rMgr.intToByte(rMgr.register[0]),3);
    }

    private void STL(int data) {
        rMgr.setMemory(data,rMgr.intToByte(rMgr.register[2]),3);
    }

    private void COMP(int data) {
        rMgr.register[9] = rMgr.register[0] - data;
    }

    private void JEQ(int data) {
        if(0 == rMgr.register[9])
            rMgr.register[8]=data;
    }

    private void JLT(int data) {
        if(rMgr.register[9]<0)
            rMgr.register[8]=data;
    }

    private void JSUB(int data) {
        rMgr.register[2] = rMgr.register[8];
        rMgr.register[8] = data;
    }
    private void J(int data) { //unstable
        rMgr.register[8] = data;
    }

    private void RSUB() {
        rMgr.register[8] = rMgr.register[2];
    }

    private void TD(int data) {
        rMgr.testDevice(String.format("%02X", rMgr.memory[data]));
    }

    private void RD() {
        rMgr.register[0]=rMgr.readDevice(rMgr.usingDevice,1);
    }
    private void WD() {
        char[] data = new char[1];
        data[0] = (char) rMgr.register[0];
        rMgr.writeDevice(rMgr.usingDevice,data,1);
    }



    // instruction 별로 동작을 수행하는 메소드를 정의
    // ex) public void add(){...}

    class Instruction {
        private String instruction;
        private int format;
        private byte opcode;
        private int numberOfOperand;

        public Instruction(String line) {
            String split[] = line.split("\t");
            this.instruction = split[0];
            this.format = Integer.parseInt(split[1]);
            this.opcode = (byte) Integer.parseInt(split[2], 16);
            this.numberOfOperand = Integer.parseInt(split[3]);
        }

        public String getInstruction() {
            return instruction;
        }

        public byte getOpcode() {
            return opcode;
        }
    }
}