// instruction에 따라 동작을 수행하는 메소드를 정의하는 클래스

import java.io.*;
import java.util.HashMap;

public class InstLuncher {
    ResourceManager rMgr;
    private HashMap<Byte, Instruction> instMap;

    public InstLuncher(ResourceManager resourceManager) {
        this.rMgr = resourceManager;
        this.instMap = new HashMap<Byte, Instruction>();
        try{
            File file = new File("./bin/inst.data");
            FileReader fileReader = new FileReader(file);
            BufferedReader  bufReader = new BufferedReader(fileReader);
            String line = "";
            while((line = bufReader.readLine())!=null){
                Instruction instruction = new Instruction(line);
                instMap.put(instruction.getOpcode(),instruction);
            }
        } catch(FileNotFoundException e){
            System.out.println("[Error] inst.data not exist");
        } catch(IOException e){
            System.out.println("[Error] IOError");
        }
    }
    public int getInstFormat(byte inst){
        return instMap.get(inst).format;
    }
    public String  getInst(byte inst){
        return instMap.get(inst).instruction;
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
            this.opcode = (byte) Integer.parseInt(split[2],16);
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