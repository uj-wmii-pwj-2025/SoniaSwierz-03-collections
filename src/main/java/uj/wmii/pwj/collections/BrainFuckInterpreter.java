package uj.wmii.pwj.collections;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Stack;

public class BrainFuckInterpreter implements Brainfuck {

    private final String program;
    private final PrintStream out;
    private final InputStream in;
    private final byte[] memory;
    private final HashMap<Integer, Integer> loopMap = new HashMap<>();


    public BrainFuckInterpreter(String program, PrintStream out, InputStream in, int stackSize) {
        if (program == null || program.isEmpty() || out == null || in == null || stackSize < 1)
            throw new IllegalArgumentException();

        this.program = program;
        this.out = out;
        this.in = in;
        this.memory = new byte[stackSize];
        buildLoopMap();
    }

    @Override
    public void execute() {
        int pointer = 0;
        int currentProgramChar = 0;

        while (currentProgramChar < program.length()) {
            char command = program.charAt(currentProgramChar);

            switch (command) {
                case '>':
                    pointer++;
                    break;
                case '<':
                    pointer--;
                    break;
                case '+':
                    memory[pointer]++;
                    break;
                case '-':
                    memory[pointer]--;
                    break;
                case '.':
                    out.print((char)(memory[pointer]));
                    break;
                case ',':
                    try {
                        int value = in.read();
                        memory[pointer] = (byte)value;
                    } catch (IOException e) {
                        throw new RuntimeException("in.read() failed exception");
                    }
                    break;
                case '[':
                    if (memory[pointer] == 0)
                        currentProgramChar = loopMap.get(currentProgramChar);
                    break;
                case ']':
                    if (memory[pointer] != 0)
                        currentProgramChar = loopMap.get(currentProgramChar);
                    break;
            }
            currentProgramChar++;
        }
    }

    private void buildLoopMap() {
        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i < program.length(); i++) {
            char currentChar = program.charAt(i);

            if (currentChar == '[') {
                stack.push(i);
            } else if (currentChar == ']') {
                int openBracket = stack.pop();
                loopMap.put(openBracket, i);
                loopMap.put(i, openBracket);
            }
        }
    }
}
