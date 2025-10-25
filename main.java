import java.util.*;
import java.io.*;

// Класс для хранения информации о банковской транзакции
class Transaction {
    private String type;
    private double amount;
    private String date;
    private String description;
    
    // Константы типов операций чтобы не использовать голые строки
    public static final String TYPE_DEPOSIT = "ПОПОЛНЕНИЕ";
    public static final String TYPE_WITHDRAWAL = "СНЯТИЕ";
    public static final String TYPE_ACCOUNT_OPENING = "ОТКРЫТИЕ СЧЕТА";
    public static final String TYPE_ACCOUNT_CLOSING = "ЗАКРЫТИЕ СЧЕТА";
    
    // Конструктор транзакции
    public Transaction(String type, double amount, String description) {
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.date = new Date().toString(); // ставим текущую дату
    }
    
    // Геттеры для приватных полей
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }
    public String getDescription() { return description; }
    
    // Красивый вывод транзакции
    @Override
    public String toString() {
        return String.format("%s: %.2f руб. - %s (%s)", type, amount, description, date);
    }
}

// Класс банковского счета хранит все данные и операции
class BankAccount {
    private String accountNumber;
    private String bik;
    private String kpp;
    private double balance;
    private ArrayList<Transaction> transactions;
    private boolean isOpen;
    
    // Создаем новый счет с нулевым балансом
    public BankAccount(String accountNumber, String bik, String kpp) {
        this.accountNumber = accountNumber;
        this.bik = bik;
        this.kpp = kpp;
        this.balance = 0;
        this.transactions = new ArrayList<>();
        this.isOpen = true;
        this.transactions.add(new Transaction(Transaction.TYPE_ACCOUNT_OPENING, 0, "Открытие счета"));
    }
    
    // геттеры для атрибутов
    public String getAccountNumber() { return accountNumber; }
    public String getBik() { return bik; }
    public String getKpp() { return kpp; }
    public double getBalance() { return balance; }
    public ArrayList<Transaction> getTransactions() { return transactions; }
    public boolean isOpen() { return isOpen; }
    
    // Пополнение счета
    public void deposit(double amount, String description) {
        balance += amount;
        transactions.add(new Transaction(Transaction.TYPE_DEPOSIT, amount, description));
    }
    
    // Снятие денег
    public void withdraw(double amount, String description) {
        balance -= amount;
        transactions.add(new Transaction(Transaction.TYPE_WITHDRAWAL, amount, description));
    }
    
    // Закрытие счета
    public void close() {
        isOpen = false;
        transactions.add(new Transaction(Transaction.TYPE_ACCOUNT_CLOSING, 0, "Закрытие счета"));
    }
}

public class Main {
    // Текущий активный счет
    private static BankAccount currentAccount = null;
    // Список всех счетов
    private static ArrayList<BankAccount> accounts = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    
    // Константы меню
    private static final int MENU_OPEN_ACCOUNT = 1;
    private static final int MENU_DEPOSIT = 2;
    private static final int MENU_WITHDRAW = 3;
    private static final int MENU_BALANCE = 4;
    private static final int MENU_TRANSACTIONS = 5;
    private static final int MENU_SEARCH = 6;
    private static final int MENU_SAVE = 7;
    private static final int MENU_CLOSE_ACCOUNT = 8;
    private static final int MENU_SEARCH_ACCOUNT = 9;
    private static final int MENU_EXIT = 0;
    
    // Ограничения по суммам
    private static final double MIN_AMOUNT = 0.01;
    private static final double MAX_AMOUNT = 1000000.0;
    
    public static void main(String[] args) {
        System.out.println("=== Банковский счет ===");
        
        // Главный цикл программы
        while(true) {
            showMenu();
            
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // чистим буфер
                
                switch(choice) {
                    case MENU_OPEN_ACCOUNT:
                        openAccount();
                        break;
                    case MENU_DEPOSIT:
                        depositMoney();
                        break;
                    case MENU_WITHDRAW:
                        withdrawMoney();
                        break;
                    case MENU_BALANCE:
                        showBalance();
                        break;
                    case MENU_TRANSACTIONS:
                        showTransactions();
                        break;
                    case MENU_SEARCH:
                        searchTransactions();
                        break;
                    case MENU_SAVE:
                        saveToFile();
                        break;
                    case MENU_CLOSE_ACCOUNT:
                        closeAccount();
                        break;
                    case MENU_SEARCH_ACCOUNT:
                        searchAccountByAttributes();
                        break;
                    case MENU_EXIT:
                        System.out.println("Выход...");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Неверный пункт меню!");
                }
            } catch (InputMismatchException e) {
                System.out.println("Ошибка! Введите число от 0 до 9.");
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Неожиданная ошибка: " + e.getMessage());
            }
        }
    }

    // Показываем меню пользователю
    private static void showMenu() {
        System.out.println("\n=== ГЛАВНОЕ МЕНЮ ===");
        System.out.println("1. Открыть счет");
        System.out.println("2. Положить деньги");
        System.out.println("3. Снять деньги");  
        System.out.println("4. Показать баланс");
        System.out.println("5. Список транзакций");
        System.out.println("6. Поиск транзакций");
        System.out.println("7. Сохранить в файл (доп.)");
        System.out.println("8. Закрыть счет");
        System.out.println("9. Поиск счета по атрибутам");
        System.out.println("0. Выход");
        System.out.print("Выберите операцию: ");
    }

    // Открываем новый счет
    private static void openAccount() {
        if (currentAccount != null && currentAccount.isOpen()) {
            System.out.println("Ошибка: счет уже открыт!");
            return;
        }
        
        System.out.print("Введите номер счета: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Введите БИК: ");
        String bik = scanner.nextLine();
        System.out.print("Введите КПП: ");
        String kpp = scanner.nextLine();
        
        currentAccount = new BankAccount(accountNumber, bik, kpp);
        accounts.add(currentAccount);
        System.out.println("Счет успешно открыт. Номер счета: " + accountNumber);
    }

    // Пополнение счета
    private static void depositMoney() {
        if (!isAccountOpen()) return;
        
        System.out.print("Введите сумму для пополнения: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        
        if (!validateAmount(amount)) return;
        
        System.out.print("Введите описание операции: ");
        String description = scanner.nextLine();
        if (description.trim().isEmpty()) {
            description = "Пополнение счета";
        }
        
        currentAccount.deposit(amount, description);
        System.out.printf("Успешно пополнено на %.2f руб.%n", amount);
        System.out.printf("Текущий баланс: %.2f руб.%n", currentAccount.getBalance());
    }

    // Снятие денег со счета
    private static void withdrawMoney() {
        if (!isAccountOpen()) return;
        
        System.out.print("Введите сумму для снятия: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        
        if (!validateAmount(amount)) return;
        
        if (amount > currentAccount.getBalance()) {
            System.out.printf("Ошибка: недостаточно средств! Доступно: %.2f руб.%n", currentAccount.getBalance());
            return;
        }
        
        System.out.print("Введите описание операции: ");
        String description = scanner.nextLine();
        if (description.trim().isEmpty()) {
            description = "Снятие наличных";
        }
        
        currentAccount.withdraw(amount, description);
        System.out.printf("Успешно снято %.2f руб.%n", amount);
        System.out.printf("Текущий баланс: %.2f руб.%n", currentAccount.getBalance());
    }

    // Закрытие счета
    private static void closeAccount() {
        if (!isAccountOpen()) return;
        
        if (currentAccount.getBalance() > 0) {
            System.out.printf("Нельзя закрыть счет! На счете остались деньги: %.2f руб.%n", currentAccount.getBalance());
            System.out.println("Сначала снимите все средства.");
            return;
        }
        
        currentAccount.close();
        currentAccount = null;
        System.out.println("Счет успешно закрыт.");
        System.out.println("Для работы с банковскими операциями откройте новый счет.");
    }

    // Показываем баланс
    private static void showBalance() {
        if (!isAccountOpen()) return;
        System.out.printf("Текущий баланс: %.2f руб.%n", currentAccount.getBalance());
    }

    // Показываем историю транзакций
    private static void showTransactions() {
        if (!isAccountOpen()) return;
        
        ArrayList<Transaction> transactions = currentAccount.getTransactions();
        if (transactions.isEmpty()) {
            System.out.println("История транзакций пуста.");
        } else {
            System.out.println("История транзакций:");
            for (int i = 0; i < transactions.size(); i++) {
                System.out.println((i + 1) + ". " + transactions.get(i));
            }
        }
    }

    // Поиск транзакций по разным критериям
    private static void searchTransactions() {
        if (!isAccountOpen()) return;
        
        System.out.println("Поиск транзакций по:");
        System.out.println("1. Типу операции");
        System.out.println("2. Сумме");
        System.out.println("3. Описанию");
        System.out.print("Выберите тип поиска: ");
        
        int searchType = scanner.nextInt();
        scanner.nextLine();
        
        boolean found = false;
        ArrayList<Transaction> transactions = currentAccount.getTransactions();
        
        switch (searchType) {
            case 1:
                System.out.print("Введите тип операции (ПОПОЛНЕНИЕ/СНЯТИЕ): ");
                String type = scanner.nextLine();
                System.out.println("Результаты поиска:");
                for (Transaction t : transactions) {
                    if (t.getType().equalsIgnoreCase(type)) {
                        System.out.println(t);
                        found = true;
                    }
                }
                break;
                
            case 2:
                System.out.print("Введите сумму для поиска: ");
                double amount = scanner.nextDouble();
                scanner.nextLine();
                System.out.println("Результаты поиска:");
                final double EPSILON = 0.001; // погрешность для double
                for (Transaction t : transactions) {
                    if (Math.abs(t.getAmount() - amount) < EPSILON) {
                        System.out.println(t);
                        found = true;
                    }
                }
                break;
                
            case 3:
                System.out.print("Введите текст для поиска в описании: ");
                String searchText = scanner.nextLine();
                System.out.println("Результаты поиска:");
                for (Transaction t : transactions) {
                    if (t.getDescription().toLowerCase().contains(searchText.toLowerCase())) {
                        System.out.println(t);
                        found = true;
                    }
                }
                break;
                
            default:
                System.out.println("Неверный тип поиска");
                return;
        }
        
        if (!found) {
            System.out.println("Транзакции по заданным критериям не найдены.");
        }
    }

    // Поиск счетов по атрибутам
    private static void searchAccountByAttributes() {
        if (accounts.isEmpty()) {
            System.out.println("Нет открытых счетов для поиска.");
            return;
        }
        
        System.out.println("Поиск счета по:");
        System.out.println("1. Номеру счета");
        System.out.println("2. БИК");
        System.out.println("3. КПП");
        System.out.print("Выберите тип поиска: ");
        
        int searchType = scanner.nextInt();
        scanner.nextLine();
        
        System.out.print("Введите значение для поиска: ");
        String searchValue = scanner.nextLine();
        
        boolean found = false;
        for (BankAccount account : accounts) {
            boolean match = false;
            switch (searchType) {
                case 1:
                    match = account.getAccountNumber().equalsIgnoreCase(searchValue);
                    break;
                case 2:
                    match = account.getBik().equalsIgnoreCase(searchValue);
                    break;
                case 3:
                    match = account.getKpp().equalsIgnoreCase(searchValue);
                    break;
                default:
                    System.out.println("Неверный тип поиска");
                    return;
            }
            
            if (match) {
                System.out.println("Найден счет:");
                System.out.println("Номер: " + account.getAccountNumber());
                System.out.println("БИК: " + account.getBik());
                System.out.println("КПП: " + account.getKpp());
                System.out.println("Баланс: " + account.getBalance() + " руб.");
                System.out.println("Статус: " + (account.isOpen() ? "Открыт" : "Закрыт"));
                System.out.println("Количество транзакций: " + account.getTransactions().size());
                found = true;
                System.out.println("---");
            }
        }
        
        if (!found) {
            System.out.println("Счета с указанными атрибутами не найдены.");
        }
    }

    // Сохранение в файл
    private static void saveToFile() {
        if (!isAccountOpen()) return;
        
        try (PrintWriter writer = new PrintWriter("bank_data.txt")) {
            writer.println("=== ДАННЫЕ БАНКОВСКОГО СЧЕТА ===");
            writer.println("Номер счета: " + currentAccount.getAccountNumber());
            writer.println("БИК: " + currentAccount.getBik());
            writer.println("КПП: " + currentAccount.getKpp());
            writer.println("Баланс: " + currentAccount.getBalance() + " руб.");
            writer.println("История транзакций:");
            for (Transaction t : currentAccount.getTransactions()) {
                writer.println(t);
            }
            System.out.println("Данные успешно сохранены в файл 'bank_data.txt'");
        } catch (FileNotFoundException e) {
            System.out.println("Ошибка при сохранении файла: " + e.getMessage());
        }
    }

    // Проверка суммы на корректность
    private static boolean validateAmount(double amount) {
        if (amount < MIN_AMOUNT) {
            System.out.println("Ошибка: сумма должна быть положительной!");
            return false;
        }
        if (amount > MAX_AMOUNT) {
            System.out.println("Ошибка: сумма слишком большая!");
            return false;
        }
        return true;
    }

    // Проверка открыт ли счет
    private static boolean isAccountOpen() {
        if (currentAccount == null || !currentAccount.isOpen()) {
            System.out.println("Ошибка: счет не открыт! Сначала откройте счет.");
            return false;
        }
        return true;
    }
}
