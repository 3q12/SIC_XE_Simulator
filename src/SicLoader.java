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
        BufferedReader bufReader;
        int codeCur = 0, secLen = 0;
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
                    rMgr.symtabList.putSymbol(symbol, rMgr.programLength);
                    secLen = Integer.parseInt(line.substring(13, line.length()), 16);
                    rMgr.programLength += secLen;
                    codeCur = 0;
                } else if (recordCode == 'D') {
                    String buf = "";
                    for (int i = 1; i < line.length() - 5; i++)
                        if (line.charAt(i) >= '0' && line.charAt(i) <= '9') {
                            rMgr.symtabList.putSymbol(buf, Integer.parseInt(line.substring(i, i + 6), 16));
                            i += 5;
                            buf = "";
                        } else
                            buf += line.charAt(i);
                } else if (recordCode == 'T') {
                    while (codeCur != Integer.parseInt(line.substring(1, 7), 16)) {
                        rMgr.memory[rMgr.memCur++] = 0;
                        codeCur++;
                    }
                    for (int i = 9; i < line.length(); i += 2) {
                        rMgr.memory[rMgr.memCur++] = (byte) Integer.parseInt(line.substring(i, i + 2), 16);
                        codeCur++;
                    }
                } else if (recordCode == 'M') {
                    while (codeCur != secLen) {
                        rMgr.memory[rMgr.memCur++] = 0;
                        codeCur++;
                    }
                    mRec.add(new Modify(Integer.parseInt(line.substring(1, 7), 16),
                            Integer.parseInt(line.substring(7, 9)),
                            rMgr.programLength - secLen,
                            line.substring(9, line.length())));
                }
            }
            // update modified record
            for (int i = 0; i < mRec.size(); i++) {
                Modify modify = mRec.get(i);
                int addr = modify.addr + modify.sectionAddr;
                byte[] originalMem = rMgr.getMemory(addr, 3), changedData = new byte[3];
                String originalData = String.format("%02X%02X%02X", originalMem[0], originalMem[1], originalMem[2]);
                String newData = String.format("%06X", rMgr.symtabList.search(modify.symbol.substring(1)));
                String calculatedData = "";
                if (modify.symbol.substring(0, 1).equals("+"))
                    calculatedData = String.format("%06X", Integer.parseInt(originalData, 16) + Integer.parseInt(newData, 16));
                else if (modify.symbol.substring(0, 1).equals("-"))
                    calculatedData = String.format("%06X", Integer.parseInt(originalData, 16) - Integer.parseInt(newData, 16));
                for (int j = 0; j < 3; j++)
                    changedData[j] = (byte) Integer.parseInt(calculatedData.substring(2 * j, 2 * j + 2), 16);
                rMgr.setMemory(addr, changedData, 3);
            }
            //for watch memory
/*            for (int i = 0; i < rMgr.memCur; i++) {
                System.out.print(String.format("%02X", rMgr.memory[i]));
                if (i % 16 == 15)
                    System.out.println();
                else if (i % 4 == 3)
                    System.out.print(" ");
            }*/
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

        public Modify(int addr, int size, int sectionAddr, String symbol) {
            this.addr = addr;
            this.size = size;
            this.sectionAddr = sectionAddr;
            this.symbol = symbol;
        }
    }
}
