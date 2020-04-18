import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Stack;

class Main extends JApplet implements ActionListener {
    private JTextField textField = new JTextField("", 20);

    private Stack<BigDecimal> numberStack = null;     //用来存放数字
    private Stack<Character> symbolStack = null;    //存放运算符和括号
    String input = "";        //输入的 式子

    private int scale; // 进行除法出现无线循环小数时保留的精度

    public Main(int scale) {
        super();
        this.scale = scale;
    }

    public Main() {
        this(32);
    }

    public void init()     //重写Applet里边的init方法
    {
        textField.setFont(new Font("宋体", Font.PLAIN, 50));//设置字体格式
        textField.setEditable(false);           //设置文本框不可更改
        Container C = getContentPane();         //获得容器的内容
        JButton[] b = new JButton[21];
        JPanel panel = new JPanel();
        JPanel panel1 = new JPanel();
        panel1.add(textField);
        panel.setLayout(new GridLayout(4, 5, 5, 5));//设置按键四行四列，边距为5像素
        panel1.setLayout(new FlowLayout(3));
        C.add(panel, BorderLayout.CENTER);
        C.add(panel1, BorderLayout.NORTH);
        String[] name = {"7", "8", "9", "+","(", "4", "5", "6", "-",")", "1", "2", "3", "*","%",".","C","0","=","/","√"};//设置按钮
        for (int i = 0; i < 21; i++)//添加按钮
        {
            b[i] = new JButton(name[i]);
            b[i].setBackground(Color.white);
            b[i].setForeground(Color.BLACK);  //设置按键颜色
            b[i].setFont(new Font("宋体", Font.PLAIN, 16));//设置字体格式与大小
            panel.add(b[i]);//将按键添加到界面
            b[i].addActionListener(this);
        }
        panel1.add(b[16]);
        b[16].setPreferredSize(new Dimension(65, 65));
    }

    public void actionPerformed(ActionEvent e) {
        int cnt = 0;
        String actionCommand = e.getActionCommand();
        if (actionCommand.equals("C")) {
            input = "";
        }else if (actionCommand.equals("="))//当监听到等号时，则处理 input
        {
            Main main = new Main();
            String numStr = textField.getText();
            BigDecimal result = main.caculate(numStr);
            numStr = main.removeStrSpace(numStr);
            textField.setText(numStr+"="+result);
            cnt = 1;
        } else
            input += actionCommand;
        if (cnt == 0)
            textField.setText(input);
    }

    //计算结果
    public BigDecimal caculate(String numStr) {
        numStr = removeStrSpace(numStr); // 去除空格
        // 如果算术表达式尾部没有‘=’号，则在尾部添加‘=’，表示结束符
        if (numStr.length() > 1
                && !"=".equals(numStr.charAt(numStr.length() - 1) + "")) {
            numStr += "=";
        }
        // 检查表达式是否合法
        if (!isStandard(numStr)) {
            return null;
        }
        // 初始化栈
        if (numberStack == null) {
            numberStack = new Stack<BigDecimal>();
        }
        numberStack.clear();
        if (symbolStack == null) {
            symbolStack = new Stack<Character>();
        }
        symbolStack.clear();
        // 用于缓存数字，因为数字可能是多位的
        StringBuffer temp = new StringBuffer();
        // 从表达式的第一个字符开始处理
        for (int i = 0; i < numStr.length(); i++) {
            char ch = numStr.charAt(i); // 获取一个字符
            if (isNumber(ch)) { // 若当前字符是数字
                temp.append(ch); // 加入到数字缓存中
            } else { // 非数字的情况
                String tempStr = temp.toString(); // 将数字缓存转为字符串
                if (!tempStr.isEmpty()) {
                    // long num = Long.parseLong(tempStr); // 将数字字符串转为长整型数
                    BigDecimal num = new BigDecimal(tempStr);
                    numberStack.push(num); // 将数字压栈
                    temp = new StringBuffer(); // 重置数字缓存
                }
                // 判断运算符的优先级，若当前优先级低于栈顶的优先级，则先把计算前面计算出来
                while (!comparePri(ch) && !symbolStack.empty()) {
                    BigDecimal b = numberStack.pop(); // 出栈，取出数字，后进先出
                    BigDecimal a = numberStack.pop();
                    // 取出运算符进行相应运算，并把结果压栈进行下一次运算
                    switch ((char) symbolStack.pop()) {
                        case '+':
                            numberStack.push(a.add(b));
                            break;
                        case '-':
                            numberStack.push(a.subtract(b));
                            break;
                        case '*':
                            numberStack.push(a.multiply(b));
                            break;
                        case '/':
                            try {
                                numberStack.push(a.divide(b));
                            } catch (java.lang.ArithmeticException e) {
                                // 进行除法出现无限循环小数时，就会抛异常，此处设置精度重新计算
                                numberStack.push(a.divide(b, this.scale,
                                        BigDecimal.ROUND_HALF_EVEN));
                            }
                            break;
                        default:
                            break;
                    }
                } // while循环结束
                if (ch != '=') {
                    symbolStack.push(new Character(ch)); // 符号入栈
                    if (ch == ')') { // 去括号
                        symbolStack.pop();
                        symbolStack.pop();
                    }
                }
            }
        } // for循环结束

        return numberStack.pop(); // 返回计算结果
    }

    //去除字符串中的所有空格
    private String removeStrSpace(String str) {
        return str != null ? str.replaceAll(" ", "") : "";
    }

    //判断输入的式子是否合法
    private boolean isStandard(String numStr) {
        if (numStr == null || numStr.isEmpty()) // 表达式不能为空
            return false;
        Stack<Character> stack = new Stack<Character>(); // 用来保存括号，检查左右括号是否匹配
        boolean b = false; // 用来标记'='符号是否存在多个
        for (int i = 0; i < numStr.length(); i++) {
            char n = numStr.charAt(i);
            // 判断字符是否合法
            if (!(isNumber(n) || "(".equals(n + "") || ")".equals(n + "")
                    || "+".equals(n + "") || "-".equals(n + "")
                    || "*".equals(n + "") || "/".equals(n + "") || "=".equals(n
                    + ""))) {
                return false;
            }
            // 将左括号压栈，用来给后面的右括号进行匹配
            if ("(".equals(n + "")) {
                stack.push(n);
            }
            if (")".equals(n + "")) { // 匹配括号
                if (stack.isEmpty() || !"(".equals((char) stack.pop() + "")) // 括号是否匹配
                    return false;
            }
            // 检查是否有多个'='号
            if ("=".equals(n + "")) {
                if (b)
                    return false;
                b = true;
            }
        }
        // 可能会有缺少右括号的情况
        if (!stack.isEmpty())
            return false;
        // 检查'='号是否不在末尾
        if (!("=".equals(numStr.charAt(numStr.length() - 1) + "")))
            return false;
        return true;
    }

    //判断是否是数字
    private boolean isNumber(char num) {
        if ((num >= '0' && num <= '9') || num == '.')
            return true;
        return false;
    }

    //比较优先级
    private boolean comparePri(char symbol) {
        if (symbolStack.empty()) { // 空栈返回ture
            return true;
        }

        // 符号优先级说明（从高到低）:
        // 第1级: (
        // 第2级: * /
        // 第3级: + -
        // 第4级: )

        char top = (char) symbolStack.peek(); // 查看堆栈顶部的对象，注意不是出栈
        if (top == '(') {
            return true;
        }
        // 比较优先级
        switch (symbol) {
            case '(': // 优先级最高
                return true;
            case '*': {
                if (top == '+' || top == '-') // 优先级比+和-高
                    return true;
                else
                    return false;
            }
            case '/': {
                if (top == '+' || top == '-') // 优先级比+和-高
                    return true;
                else
                    return false;
            }
            case '+':
                return false;
            case '-':
                return false;
            case ')': // 优先级最低
                return false;
            case '=': // 结束符
                return false;
            default:
                break;
        }
        return true;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("计算器");//创建顶级窗口
        frame.setResizable(false);
        Main applet = new Main();
        frame.getContentPane().add(applet, BorderLayout.CENTER);
        applet.init();     //applet的init方法
        applet.start();    //线程开始
        frame.setSize(400, 450);  //设置窗口大小
        frame.pack();//设置自适应大小
        frame.setVisible(true);    //设置窗口可见
    }
}
