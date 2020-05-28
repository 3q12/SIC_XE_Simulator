import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * SicLoader�� ���α׷��� �ؼ��ؼ� �޸𸮿� �ø��� ������ �����Ѵ�. �� �������� linker�� ���� ���� �����Ѵ�.
 * <br><br>
 * SicLoader�� �����ϴ� ���� ���� ��� ������ ����.<br>
 * - program code�� �޸𸮿� �����Ű��<br>
 * - �־��� ������ŭ �޸𸮿� �� ���� �Ҵ��ϱ�<br>
 * - �������� �߻��ϴ� symbol, ���α׷� �����ּ�, control section �� ������ ���� ���� ���� �� ����
 */
public class SicLoader {
    ResourceManager rMgr;

    public SicLoader(ResourceManager resourceManager) {
        // �ʿ��ϴٸ� �ʱ�ȭ
        setResourceManager(resourceManager);
    }

    /**
     * Loader�� ���α׷��� ������ �޸𸮸� �����Ų��.
     *
     * @param resourceManager
     */
    public void setResourceManager(ResourceManager resourceManager) {
        this.rMgr = resourceManager;
    }

    /**
     * object code�� �о load������ �����Ѵ�. load�� �����ʹ� resourceManager�� �����ϴ� �޸𸮿� �ö󰡵��� �Ѵ�.
     * load�������� ������� symbol table �� �ڷᱸ�� ���� resourceManager�� �����Ѵ�.
     *
     * @param objectCode �о���� ����
     */
    public void load(File objectCode) {
        String line = "";
        BufferedReader bufReader = null;
        int codeCur = 0, secLen = 0;
        char tBuf = 'G';
        Boolean zeroFill=false;
        ArrayList<Modify> mRec = new ArrayList<Modify>();
        try {
            bufReader = new BufferedReader(new FileReader(objectCode));
            while ((line = bufReader.readLine()) != null) {
                if (line.length() == 0) continue;
                char recordCode = line.charAt(0);
                if (recordCode == 'H') {
                    String symbol = line.substring(1, 7).trim();
                    if (rMgr.programName.isEmpty()) {
                        rMgr.programName = symbol;
                        rMgr.startAddr = Integer.parseInt(line.substring(7, 12));
                    }
                    rMgr.symtabList.symbolList.add(symbol);
                    rMgr.symtabList.addressList.add(rMgr.programLength);
                    secLen = Integer.parseInt(line.substring(13, line.length()), 16);
                    rMgr.programLength += secLen;
                    codeCur = 0;
                } else if (recordCode == 'D') {
                    String buf = "";
                    for (int i = 1; i < line.length() - 5; i++)
                        if (line.charAt(i) >= '0' && line.charAt(i) <= '9') {
                            rMgr.symtabList.symbolList.add(buf);
                            rMgr.symtabList.addressList.add(Integer.parseInt(line.substring(i, i + 6), 16));
                            i += 5;
                            buf = "";
                        } else
                            buf += line.charAt(i);
                } else if (recordCode == 'T') {
                    int i = 9;
                    int startAddr = Integer.parseInt(line.substring(1, 7), 16);
                    if (tBuf != 'G') {
                        rMgr.memory[rMgr.memCur++] = (char) (tBuf + (char) Integer.parseInt(line.substring(9, 11), 16));
                        i = 11;
                        startAddr++;
                        tBuf = 'G';
                        codeCur++;
                        zeroFill=false;
                    }
                    while (codeCur != startAddr) {
                        rMgr.memory[rMgr.memCur++] = 0;
                        codeCur += 2;
                    }
                    for (; i < line.length(); i += 4) {
                        if (i + 4 > line.length()) {
                            System.out.println(line.substring(i, i + 2));
                            tBuf = (char) ((char) Integer.parseInt(line.substring(i, i + 2), 16) << 8);
                            codeCur++;
                        } else {
                            rMgr.memory[rMgr.memCur++] = (char) Integer.parseInt(line.substring(i, i + 4), 16);
                            System.out.println(line.substring(i, i + 4));
                            codeCur += 2;
                        }
                    }
                } else if (recordCode == 'M') {
                    if (tBuf != 'G' && codeCur != secLen && !zeroFill) {
                        rMgr.memory[rMgr.memCur++] = tBuf;
                        tBuf = 'G';
                        codeCur++;
                    }
                    while (codeCur != secLen) {
                        if(secLen-codeCur==1){
                            tBuf = 0;
                            zeroFill = true;
                            codeCur++;
                            break;
                        }
                        rMgr.memory[rMgr.memCur++] = 0;
                        codeCur += 2;
                    }
                    mRec.add(new Modify(Integer.parseInt(line.substring(1, 7), 16),
                            Integer.parseInt(line.substring(7, 9)),
                            rMgr.programLength - secLen,
                            line.substring(9, line.length())));/*
                    System.out.println(rMgr.programLength);
                    System.out.println(rMgr.memCur);
                    System.out.println(line.substring(1, 7));
                    System.out.println("==========================");*/
                }
            }
            for (int i = 0; i < mRec.size(); i++) {
                Modify modify = mRec.get(i);
                System.out.print("original addr");
                System.out.println(String.format("%02X",modify.addr+modify.sectionAddr));
                int addr = modify.addr + modify.sectionAddr;
                System.out.println(modify.addr%2 + modify.sectionAddr%2);
                char[] tmp = rMgr.getMemory(addr,2);
                for(int j = 0; j<tmp.length;j++){
                    System.out.print(String.format("%04X", (int)tmp[j]));
                }
                System.out.println();
            }
            for (int i = 0; i < rMgr.memCur; i++){
                System.out.print(String.format("%04X", (int) rMgr.memory[i]));
                if(i%8==0 && i>8)
                    System.out.println();
                else if(i%2==0)
                    System.out.print(" ");
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    public class Modify {
        int addr;
        int size;
        int sectionAddr;
        String symbol;

        public Modify(int addr, int size,int sectionAddr, String symbol) {
            this.addr = addr;
            this.size = size;
            this.sectionAddr = sectionAddr;
            this.symbol = symbol;
        }
    }
}
