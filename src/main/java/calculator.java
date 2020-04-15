import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.Stack;
class Main extends JApplet implements ActionListener {
    private JTextField textField = new JTextField("", 20);
    String input = "";        //输入的 式子
    boolean flag = true;

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
            String in = compute(input);
            Double convert = calculateSuffix(in); //将结果转换为double
            String result = String.valueOf(convert);
            textField.setText(input+"="+result);
            cnt = 1;
        } else
            input += actionCommand;//数字为了避免多位数的输入 不需要加空格
        if (cnt == 0)
            textField.setText(input);
    }

    /*将中缀表达式转换成后缀表达式并加上分隔符*/
    private static String compute(String string)//
    {
        StringBuilder stringBuilder = new StringBuilder();  //存放数字
        Stack<Character> opr = new Stack<Character>(); //存放运算符
        char[] chars = string.toCharArray();
        int size = chars.length;
        int i = 0;
        while (i < size) {
            switch (chars[i]) {
                case '(':
                    opr.push(chars[i]);
                    break;
                case ')':
                    while (opr.peek() != '(') {
                        stringBuilder.append(opr.pop()).append("#");
                    }
                    opr.pop();
                    break;
                case '+':
                case '-':
                    while (!opr.isEmpty() && opr.peek() != '(') {
                        stringBuilder.append(opr.pop()).append("#");
                    }
                    opr.push(chars[i]);
                    break;
                case '*':
                case '/':
                    if (opr.isEmpty()) {
                        opr.push(chars[i]);
                        break;
                    }

                    while (opr.peek() == '*' || opr.peek() == '/') {
                        stringBuilder.append(opr.pop()).append("#");
                    }
                    opr.push(chars[i]);
                    break;
                default:
                    int num = chars[i] - '0';
                    while ((i + 1) < size && chars[i + 1] >= '0' && chars[i + 1] <= '9'|| num == '.') {
                        num = num * 10 + chars[i + 1] - '0';
                        i++;
                    }
                    stringBuilder.append(num).append("#");

                    break;

            }

            i++;
        }

        while (!opr.isEmpty()) {
            stringBuilder.append(opr.pop());
        }
        return stringBuilder.toString();
    }
    //取消分隔符拆分字符串，计算后缀表达式
    public static double calculateSuffix(String string) {

        String[] items = string.split("#");
        Stack<Double> stack = new Stack<Double>();

        for (String item : items) {
            if ("+-*/".contains(item)) {
                double num_2 = stack.pop();
                double num_1 = stack.pop();

                stack.push(calculateOpr(num_1, item, num_2));
                continue;
            }
            stack.push(Double.parseDouble(item));
        }


        return stack.pop();
    }
    private static double calculateOpr(double num_1, String item, double num_2) {
        double num;
        if (Objects.equals(item, "+")) {
            num = num_1 + num_2;
            return num;
        }

        if (Objects.equals(item, "-")) {
            num = num_1 - num_2;
            return num;
        }

        if (Objects.equals(item, "*")) {
            num = num_1 * num_2;
            return num;
        }

        if (Objects.equals(item, "/")) {
            num = num_1 / num_2;
            return num;
        }

        return 0;
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Counter");//创建顶级窗口
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
