import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * ���� ����� ����������� �������������.
 * � ���� ������ ���������� �������� ���� ������ �������.
 * � ���� ������ ����������� ������ ��������������� ��������.
 * @author �������
 *
 */
public class UserClientCore implements IUserClientCore {
	
	// TODO: ����������� ����� ������ (��� ����� ������ ������������� ������� ������ ����������� �� �������)
	// TODO: ����������� �������� ��������
	// TODO: ����������� ��������� ��������� ��� ������� ������������
	// TODO: ���������, �������� �� �����������
	
	// ���������� ��������� ��� ���� ���������� ���������.
	// ����� ������ ��� ��������� ������ � ������� add � 1 �������,
	// ��� ��������� �� �� 1-��������� �������, ����� �������� �� ��� �������...
	// � ���, ������, ����� ������ ������ ��������� ������� ��� ��������� � �����
	
	private String appName = "Vehicle App";
	
	private String currentCollectionName;
	
	private IUserClientReceiver receiverModule;
	private ILogicRequestFactory logicRequestFactory;
	private IUserClientRequestSender requestSenderModule;
	private IUserClientInputModule userInputModule;
	
	private Stack<IScript> scripts;
	
	private static final Set<String> serverCommands = createServerCommandsMap();
	private static Set<String> createServerCommandsMap() {
		Set<String> result = new HashSet<>();
		result.add("add");
		result.add("info");
		result.add("update");
		result.add("sort");
		result.add("show");
		result.add("remove_by_id");
		result.add("clear");
		result.add("remove_greater");
		result.add("remove_lower");
		result.add("max");
		result.add("group_counting_by_engine_power");
		result.add("count_greater_than_engine_power");
		return Collections.unmodifiableSet(result);
	}
	
	private static final Map<String, String> commandDescriptions = createCommandDescriptionsMap();
    private static Map<String, String> createCommandDescriptionsMap() {
        Map<String, String> result = new HashMap<>();
        result.put("add", "Adding a new Vehicle to your Collection");
        result.put("info", "Getting the main information about your Collection");
        result.put("update" , "Updating the vehicle with the given id");
        result.put("sort" , "Sorting the Vehicles by the given key");
        result.put("show" , "Displaying the collection");
        result.put("remove_by_id" , "Removeing the vehicle with the given id");
        result.put("clear" , "Removing all of the Vehicles from your collection");
        result.put("remove_greater" , "Removing vehicles greater than given");
        result.put("remove_lower" , "Removing vehicles lower than given");
        result.put("max" , "Getting the greatest Element by the given key");
        result.put("group_counting_by_engine_power", "Grouping counting by engine power");
		result.put("count_greater_than_engine_power" , "Counting the amout of Vehicles with greater "
				+ "engine_power that given");
        result.put("use", "Using a Vehicle Collection by the given name as a current collection");
        result.put("execute_script", "Executing the script in the given file");
        result.put("exit", "Quitting the Vehicle App");
		return Collections.unmodifiableMap(result);
    }
    
	private SocketAddress socketAddress;
	
	private DatagramChannel datagramChannel;
	
	private boolean userLogged;
	
	private String currentUserName;
	
	private String currentUserPassword;
	
	
	public UserClientCore(InetAddress serverAddress, int serverPort) throws IOException {
		this.socketAddress = new InetSocketAddress(serverAddress, serverPort);
		this.datagramChannel = DatagramChannel.open();
		this.scripts = new Stack<>();
		/**
		 * ����� ��� ������ default �������� �����-������ ����� �������,
		 * ���� ���� � ������������ ����� �� ����, �� ��������� ������� �������,
		 * ���� �� ������� ��� ������� ���������
		 */
		this.currentCollectionName = "default";
		
		this.datagramChannel.configureBlocking(false);
		
		this.userLogged = false;
		this.currentUserName = "default";
		this.currentUserPassword = "default";
	}
	
	/**
	 * �������� ����
	 */
	public void run() {
		ArrayList<String> commandAndArguments;
		IScript newScript = null;
		while (true) {
				try {
					commandAndArguments = this.getCommandAndArguments();
				} catch (InputDeniedByUserException e1) {
					System.out.println(this.getStructureDescription() + " > User command input | "
							+ "Input calcelled by user");

					continue;
				} catch (Exception e2) {
					System.out.println(this.getStructureDescription() + " > Command input | Input calcelled - " + e2.getMessage());

					continue;
				}
			if (commandAndArguments.get(0).toLowerCase().equals("exit")) {
				//TODO: ��������� �����
				System.out.println(this.getStructureDescription() + " | Bye, Have a good one!");
				return;
			}
			else if (commandAndArguments.get(0).toLowerCase().equals("help")) {
				// ������� ����������� �� �������
				System.out.println(this.getStructureDescription() + " > Help | Here are some of the commands for you to take advantage of:");
				for (String key : commandDescriptions.keySet()) {
			        System.out.println("| Command '" + key + "' >>> " + commandDescriptions.get(key));
			    }
			}
			else if (commandAndArguments.get(0).toLowerCase().equals("use")) {
				if (commandAndArguments.size() < 2) {
					System.out.println(this.getStructureDescription() + " > Command input > Using a collection | "
							+ "You must specify the name of the collection that you want to use - e.g. "
							+ "'use collectionName'");
					continue;
				}
				if (!this.isValidCollectionName(commandAndArguments.get(1))) {
					System.out.println(this.getStructureDescription() + " > Command input > Using a collection | "
							+ "The name of the collection you gave is invalid. It may only contain letters A-Z & a-z");
					continue;
				}
				this.currentCollectionName = commandAndArguments.get(1);
				System.out.println(this.getStructureDescription() + " > Command input > Using a collection | "
						+ "You are now using the collection '" + commandAndArguments.get(1) + "'");
			}
			else if (commandAndArguments.get(0).toLowerCase().equals("load")) {
				// �� ����� ����������, ������������� ����� ������� �������� � ����� ������ ��������� - �� �������
				// ������� ����������� �������� �� �������, � ��������� ���������� � �� ������� ���������� �������
				if (commandAndArguments.size() > 1) {
					try {
						String collectionAsXml = this.userInputModule.getTextFromFile(commandAndArguments.get(1));
						commandAndArguments.clear();
						commandAndArguments.add("load");
						commandAndArguments.add(collectionAsXml);
						executeServerCommand(commandAndArguments);
					} catch (FileNotFoundException e) {
						System.out.println("���������� ����� �� ����������");
						continue;
					} catch (IOException e) {
						System.out.println("������ �� ���������� ����� ����������, ��������� ��� �����������, "
								+ "����������� �� ������ � ��� ������");
						continue;
					}
				} else {
					System.out.println("������� ���� ������ ���������");
				}
				
			}
			else if (commandAndArguments.get(0).toLowerCase().equals("execute_script")) {
				// ������� ����������� �� �������
				if (commandAndArguments.size() > 1) {
					try {
						newScript = this.userInputModule.getScript(commandAndArguments.get(1));
					} catch (IOException e) {
						System.out.println(this.getStructureDescription() + " > File access error | This file is not available for reading");
						continue;
					}
					
					// ������������ ��� �������, ���� ����� ���� � ��� �� ����, �� ������� ������� LOOP, ���������
					boolean hasLoops = false;
					for (IScript currentScript : this.scripts) {
						if (currentScript.getFile().getAbsolutePath().equals(newScript.getFile().getAbsolutePath())) {
							hasLoops = true;
							break;
						}
					}
					
					if (hasLoops) {
						System.out.println(this.getStructureDescription() + " > Executing a script > Script Error |"
								+ " Your script contains loops, so it could not be fully executed");
						this.scripts.clear();
						continue;
					}
					
					this.scripts.push(newScript);
				}
				else {
					System.out.println(this.getStructureDescription() + " > Executing a script | You must specify the "
							+ "name of the script that you want to execute, e.g. 'execute_script ./filename.txt'");
				}
				
			}
			else if (commandAndArguments.get(0).toLowerCase().equals("login")) {
				// ��������� 1 � 2, ������ � 0, �������� ������� � �������. �� ���������� ������������ � .matches("^[a-zA-Z]+$");
				if (commandAndArguments.size() < 3) {
					System.out.println(this.getStructureDescription() + " Logging in error > | In order to log in "
							+ "use 'login <username> <password>'");
					continue;
				}
				if (commandAndArguments.get(1).length() < 3 || commandAndArguments.get(1).length() > 20) {
					System.out.println(this.getStructureDescription() + " Logging in error > Invalid username > | The username must be 3-20 characters");
					continue;
				}
				if (!commandAndArguments.get(1).matches("^[a-zA-Z]+$")) {
					System.out.println(this.getStructureDescription() + " Logging in error > Invalid username > | The username may only contain characters a-z and A-Z");
					continue;
				}
				if (commandAndArguments.get(2).length() < 3 || commandAndArguments.get(2).length() > 40) {
					System.out.println(this.getStructureDescription() + " Logging in error > Invalid password > | The password must be 3-40 characters");
					continue;
				}
				if (!commandAndArguments.get(2).matches("^[a-zA-Z]+$")) {
					System.out.println(this.getStructureDescription() + " Logging in error > Invalid password > | The password may only contain characters a-z and A-Z");
					continue;
				}
				// ��� ��� ������ ��������� ��� ������ ���������, � � �� �����
				ILogicServerRequest request = new LogicServerRequestCheckPassword("StringThatDontMatter", commandAndArguments.get(1), commandAndArguments.get(2));
				try {
					this.requestSenderModule.sendRequest(request);
					IResponse response = this.receiverModule.receiveResponse();
					if (response.isRequestSuccessful()) {
						this.userLogged = true;
						this.currentUserName = commandAndArguments.get(1);
						this.currentUserPassword = commandAndArguments.get(2);
						System.out.println(this.getStructureDescription() + " > Logging in | Success");
					}
					else {
						System.out.println(this.getStructureDescription() + " > Logging in > Failure | Incorrect login or password - try again");
					}
				} catch (IOException e) {
					System.out.println(this.getStructureDescription() + " > Error | Server is currently anavailable - please try again later");
				} catch (NoServerResponseException e) {
					System.out.println(this.getStructureDescription() + " > Error | Server is currently anavailable - please try again later");
				}
			}
			else if (commandAndArguments.get(0).toLowerCase().equals("logout")) {
				if (!this.userLogged) {
					System.out.println(this.getStructureDescription() + " > Logging out | You must log in if you want to log out");
					continue;
				}
				
				System.out.println(this.getStructureDescription() + " > Logging out | You've successfully logged out");
				this.userLogged = false;
				this.currentUserName = "default";
				this.currentUserPassword = "default";
			}
			else if (commandAndArguments.get(0).toLowerCase().equals("delete_account")) {
				if (!this.userLogged) {
					System.out.println(this.getStructureDescription() + " > Deleting the account | You must log in if you want to delete an account");
					continue;
				}
				ILogicServerRequest request = new LogicServerRequestDeleteAccount(currentCollectionName, currentUserName, currentUserPassword);
				try {
					this.requestSenderModule.sendRequest(request);
					IResponse response = this.receiverModule.receiveResponse();
					if (response.isRequestSuccessful()) {
						System.out.println(this.getStructureDescription() + " > Deleting the account > Success | Your account has been deleted");
						this.userLogged = false;
						this.currentUserName = "default";
						this.currentUserPassword = "default";
					}
				} catch (IOException e) {
					System.out.println(this.getStructureDescription() + " > Error | Server is currently anavailable - please try again later");
					e.printStackTrace();
				} catch (NoServerResponseException e) {
					System.out.println(this.getStructureDescription() + " > Error | Server is currently anavailable - please try again later");
				}
				
			}
			else if (commandAndArguments.get(0).toLowerCase().equals("change_password")) {
				if (!this.userLogged) {
					System.out.println(this.getStructureDescription() + " > Changing the password | You must log in if you want to change the password");
					continue;
				}
				
				// ������� � 0, 1 �������� - ������ ������, 2 �������� - ����� ������
				if (commandAndArguments.size() < 3) {
					System.out.println(this.getStructureDescription() + " Changing password error > | In order to change password"
							+ "use 'change_password <old password> <new password>'");
					continue;
				}
				if (!commandAndArguments.get(1).toLowerCase().equals(this.currentUserPassword)) {
					System.out.println(this.getStructureDescription() + " Changing password error > | Your old password is incorrect - "
							+ "if you think that it's a mistake please try to re log in");
					continue;
				}
				if (commandAndArguments.get(2).length() < 3 || commandAndArguments.get(2).length() > 40) {
					System.out.println(this.getStructureDescription() + " Changing password error > Invalid password > | The password must be 3-40 characters");
					continue;
				}
				if (!commandAndArguments.get(2).matches("^[a-zA-Z]+$")) {
					System.out.println(this.getStructureDescription() + " Changing password error > Invalid password > | The password may only contain characters a-z and A-Z");
					continue;
				}
				// ���� �� ��� �� ������ ������ ���������� � ����� ��������, ����� ���������� ������.
				ILogicServerRequest request = new LogicServerRequestChangePassword(currentCollectionName, currentUserName, currentUserPassword, commandAndArguments.get(2));
				try {
					this.requestSenderModule.sendRequest(request);
					IResponse response = this.receiverModule.receiveResponse();
					if (response.isRequestSuccessful()) {
						System.out.println(this.getStructureDescription() + " Changing the password > Success | Your password has been changed successfully");
						this.currentUserPassword = commandAndArguments.get(2);
						continue;
					}
					System.out.println(this.getStructureDescription() + " Changing the password > Failure | Your "
							+ "password could not be changed - some data of your account changed since your "
							+ "last login - please re log in");
					this.userLogged = false;
					this.currentUserName = "default";
					this.currentUserPassword = "default";
				} catch (IOException e) {
					System.out.println(this.getStructureDescription() + " > Error | Server is currently anavailable - please try again later");
				} catch (NoServerResponseException e) {
					System.out.println(this.getStructureDescription() + " > Error | Server is currently anavailable - please try again later");
				}
				
				
			}
			else if (commandAndArguments.get(0).toLowerCase().equals("register")) {
				// ��������� 1 � 2, ������ � 0, �������� ������� � �������. �� ���������� ������������ � .matches("^[a-zA-Z]+$");
				if (commandAndArguments.size() < 3) {
					System.out.println(this.getStructureDescription() + " Registration error > | In order to register "
							+ "use 'register <username> <password>'");
					continue;
				}
				if (commandAndArguments.get(1).length() < 3 || commandAndArguments.get(1).length() > 20) {
					System.out.println(this.getStructureDescription() + " Registration error > Invalid username > | The username must be 3-20 characters");
					continue;
				}
				if (!commandAndArguments.get(1).matches("^[a-zA-Z]+$")) {
					System.out.println(this.getStructureDescription() + " Registration error > Invalid username > | The username may only contain characters a-z and A-Z");
					continue;
				}
				if (commandAndArguments.get(2).length() < 3 || commandAndArguments.get(2).length() > 40) {
					System.out.println(this.getStructureDescription() + " Registration error > Invalid password > | The password must be 3-40 characters");
					continue;
				}
				if (!commandAndArguments.get(2).matches("^[a-zA-Z]+$")) {
					System.out.println(this.getStructureDescription() + " Registration error > Invalid password > | The password may only contain characters a-z and A-Z");
					continue;
				}
				// ���� �� ���, �� �� ���� � ����������� ���� ��������������
				// ������ �� ������������
				ILogicServerRequest request = new LogicServerRequestCreateUser("thisStringDontMatter", commandAndArguments.get(1), commandAndArguments.get(2));
				try {
					this.requestSenderModule.sendRequest(request);
					IResponse response = this.receiverModule.receiveResponse();
					if (response.isRequestSuccessful()) {
						System.out.println(this.getStructureDescription() + " > Registration > Success | Congratulations " + commandAndArguments.get(1) + " on creating your new VehicleApp account. You can now log in with 'login <username> <password>'");
					}
					else {
						System.out.println(this.getStructureDescription() + " > Registration > Failure | Your registration failed successfully. User with such name already exist, choose something different");
					}
				} catch (IOException e) {
					System.out.println(this.getStructureDescription() + " > Error | Server is currently anavailable - please try again later");
				} catch (NoServerResponseException e) {
					System.out.println(this.getStructureDescription() + " > Error | Server is currently anavailable - please try again later");
				}
				
			}
			
			else if (commandAndArguments.get(0).toLowerCase().equals("public")) {
				ILogicServerRequest request = new LogicServerRequestMakePublic(currentCollectionName,
						currentUserName, currentUserPassword);
				try {
					this.requestSenderModule.sendRequest(request);
					IResponse response = this.receiverModule.receiveResponse();
					
					if (response.isRequestSuccessful()) {
						System.out.println(this.getStructureDescription() + " > Success | You just made your collection editable by everyone");
					}
					else {
						System.out.println(this.getStructureDescription() + " > Failure | Sorry, we couldn't access that collection");
					}
				} catch (IOException e) {
					System.out.println(this.getStructureDescription() + " > Error | Server is currently anavailable - please try again later");
					e.printStackTrace();
				} catch (NoServerResponseException e) {
					System.out.println(this.getStructureDescription() + " > Error | Server is currently anavailable - please try again later");
				}
				
			}
			
			else if (commandAndArguments.get(0).toLowerCase().equals("viewonly")) {
				ILogicServerRequest request = new LogicServerRequestMakeViewOnly(currentCollectionName,
						currentUserName, currentUserPassword);
				try {
					this.requestSenderModule.sendRequest(request);
					IResponse response = this.receiverModule.receiveResponse();
					
					if (response.isRequestSuccessful()) {
						System.out.println(this.getStructureDescription() + " > Success | You just made your collection editable view-only");
					}
					else {
						System.out.println(this.getStructureDescription() + " > Failure | Sorry, we couldn't access that collection");
					}
				} catch (IOException e) {
					System.out.println(this.getStructureDescription() + " > Error | Server is currently anavailable - please try again later");
					e.printStackTrace();
				} catch (NoServerResponseException e) {
					System.out.println(this.getStructureDescription() + " > Error | Server is currently anavailable - please try again later");
				}
				
			}
			
			else {
				// ������� �������� �������� �� �������
				
				// ���� ������������ �� ������������, �� ��������
				if (!this.userLogged) {
					System.out.println(this.getStructureDescription() + " | In order to execute this command you need to"
							+ " login or register. Please do so by using 'login <username> <password>' "
							+ "or 'register <username> <password>'");
					continue;
				}
				executeServerCommand(commandAndArguments);
			}
		}
	}

	/**
	 * ����� ���������� � ������������ ����� �������� + ���������,
	 * ���� ���� �� ����������� �� �������
	 * @return ������, 0 ��������� �������� �������� �������,
	 * � ����������� �������� - ���������(������������)
	 * @throws InputDeniedByUserException 
	 */
	private ArrayList<String> getCommandAndArguments() throws InputDeniedByUserException, Exception {
		ArrayList<String> toReturn = new ArrayList<>();
		if (!this.scripts.isEmpty()) {
			IScript currentScript = this.scripts.peek();
			// �������� ��������� ������ �������, ��� �������, ��������, � ����������� ����� ������
			String currentLine = currentScript.getNextLine();
			if (currentLine.length() == 0) {
				System.out.println(this.getStructureDescription() + " > Executing the script | Execution failed because your script contains blank lines");
			}
			
			String[] currentLineWithArgs = currentLine.split(" ");
			for (int i = 0; i < currentLineWithArgs.length; i++) {
				toReturn.add(currentLineWithArgs[i]);
			}
			
			if (currentLineWithArgs[0].equals("add") || currentLineWithArgs[0].equals("update") ||
					currentLineWithArgs[0].equals("remove_greater") || currentLineWithArgs[0].equals("remove_lower")) {
				// ���������� ������� �� ������� ��������� - ��� ������: ������� ���, �����
				String[] vehicleArgs;
				try {
					vehicleArgs = this.readVehicleFromScript();
				} catch (Exception e) {
					// ��������� � ������� �� �������� - �������� ������������, �������� ������.
					System.out.println(this.getStructureDescription() + " > Executing the script | Failed because of invalid arguments - " + e.getMessage());
					System.out.println(this.getStructureDescription() + " > Executing the script | Execution stopped with en error ");
					this.scripts.clear();
					throw e;
				}
				for (int i = 0; i < vehicleArgs.length; i++) {
					toReturn.add(vehicleArgs[i]);
				}
				
				
			}
			
			if (currentScript.isFinished()) {
				this.scripts.pop();
			}
			
			return toReturn;
		}
		
		// ���� �� �����, �� ���������������� ����
		return this.userInputModule.getCommandAndArgs();
	}
	
	/**
	 * ����� ��������� ��������� ������ �� ������� � ����������� ������, ���� ��� �� ���
	 * ��� ��������� ������������ ������ isValid ���������� ADialogPhase
	 */
	private String[] readVehicleFromScript() throws Exception {
		String[] arguments = new String[6];
		IScript currentScript = this.scripts.peek();
		String currentArg;
		for (int i = 0; i < 6; i++) {
			if (currentScript.isFinished()) {
				throw new Exception("not enough arguments to create a new Vehicle (6 required)");
			}
			currentArg = currentScript.getNextLine();
			arguments[i] = currentArg;
		}
		
		// ��������� �������� � �� ����������, ������ ���������
		
		ArrayList<ADialogPhase> validationPhases = new ArrayList<>();
		validationPhases.add(new ADialogPhaseStringOneOfGiven(VehicleType.toStringSet(), "Please choose the type of your new vehicle", this, "BACK"));
		validationPhases.add(new ADialogPhaseString(3, 20, "Choose a name", this, "BACK"));
		validationPhases.add(new ADialogPhaseInt(-100000000, 820, "Enter the x coordinate", this, "BACK"));
		validationPhases.add(new ADialogPhaseFloat(-538, 100000000, 10, "Enter the y coordinate", this, "BACK"));
		validationPhases.add(new ADialogPhaseInt(1, 100000000, "Enter the engine_power", this, "BACK"));
		validationPhases.add(new ADialogPhaseStringOneOfGiven(FuelType.toStringSet(), "Choose the fuel type", this, "BACK"));
		
		for (int i = 0; i < 6; i++) {
			if (!validationPhases.get(i).isValid(arguments[i])) {
				throw new Exception(validationPhases.get(i).getFailMessage(arguments[i]) + ", your script contains '" + arguments[i] + "'");
			}
		}
		
		// ���� �� ���, �� ��� ��������� ������ ��������� ����� ���������� ��
		
		return arguments;
	}
	
	/**
	 * ����� ���������� ��� �������� � � ����������� � ������,
	 * ���� ����������� � ������ ������ ������� �������� ���������.
	 * � �� ���� ���� �������������� � �������� - ������������ �������,
	 * ����������� �������, ��������� ������, ��������� ������
	 */
	private void executeServerCommand(ArrayList<String> commandAndArguments) {
		// ������� �������� �������� �� �������
		String responseAsString;
		// ������, ������
		IRequest request;
		IResponse response;
		if (UserClientCore.serverCommands.contains(commandAndArguments.get(0))) {
			try {
				// ������ ������ �� ��� ����������� ����������������� �����, ��� �� �������
				request = this.logicRequestFactory.createRequest(commandAndArguments);
			} catch (IndexOutOfBoundsException e) {
				System.out.println(this.getStructureDescription() + " > Command input | Your command could not be "
						+ "executed due the lack of arguments - use more arguments or something idk. Use 'help' for help");
				// ���� �� � �������, �� �������� ������
				this.scripts.clear();
				return;
			}	
			catch (IllegalArgumentException e) {
				// TODO: ��������� �����������
				System.out.println(this.getStructureDescription() + " > Command input | Your command could not be "
						+ "executed due to invalid arguments. Use 'help' for help");
				
				// ���� �� � �������, �� �������� ������
				this.scripts.clear();
				return;
			} catch (Exception e) {
				// ��������� �����������
				System.out.println(this.getStructureDescription() + " > Command input | Your command could not be "
						+ "executed due to invalid arguments. Use 'help' for help");
				
				// ���� �� � �������, �� �������� ������
				this.scripts.clear();
				return;
			}
			try {
				this.requestSenderModule.sendRequest(request);
			} catch (IOException e) {
				System.out.println(this.getStructureDescription() + " > Sending the request | Your request is invalid, please try again with valid arguments");				
				return;
			}
			
			try {
				response = this.receiverModule.receiveResponse();
			} catch (IOException e) {
				//TODO: �� �����
				
				System.out.println(this.getStructureDescription() + "Sorry, your request could not be executed, "
						+ "you have connection issues. Check your Wi-Fi or sometihng");
				return;
			} catch (NoServerResponseException e) {
				// ������� ����� ��� ������ �� �������
				// ���� 1 ��� �� ���������, �� ������������, ��� ����� ���� ��������� �������, ������� ��� 1 ���,
				// ���� ����� �� ����������, ������ ������������� ��������
				try {
					System.out.println(this.getStructureDescription() + " > Time out | Retrying.");
					this.requestSenderModule.sendRequest(request);
					response = this.receiverModule.receiveResponse();
				} catch (Exception e2) {
					System.out.println(this.getStructureDescription() + " > Time out | Sorry, our server is down at the moment."
							+ " We are trying our best right now to fix it. Try again later.");
					return;
				}
				
			}
			responseAsString = receiverModule.getResponseStringDescription(commandDescriptions.get(commandAndArguments.get(0)),  response);
			
			// ������������ ������-����� ������������
			System.out.println(responseAsString);
			
			// ���� ������ ������������ ������������ �������� �������� - �������� ��� �� ���� � ����� �� ��������
			if (!response.isPasswordCorrect()) {
				System.out.println(this.getStructureDescription() + " > Invalid password | Sorry, but your password is not valid. It might have changed or something. Try to re log in");
				this.userLogged = false;
				this.currentUserName = "default";
				this.currentUserPassword = "default";
			}
		}
		else {
			// ����� ������� �� ����������
			int shortest = 999999999;
			String probableCommand = "add";
			int lev;
			for (String currentCommand: serverCommands) {
				lev = levenstain(currentCommand, commandAndArguments.get(0));
				if (lev < shortest) {
					probableCommand = currentCommand;
					shortest = lev;
				}
			}
			
			System.out.println("There's no such command as '" + commandAndArguments.get(0) +
					"'. Did you mean '" + probableCommand + "'?");
			
			return;
		}
	}
	

	// �������� ���������� �����������
	private static int levenstain(String str1, String str2) {
        // ������� ������ ���� ���������� �����, �.�. �������� ��� ������ (��� �������) ����� �
		// ��� �� ������� (��. �������� ���������� �����������)
        int[] Di_1 = new int[str2.length() + 1];
        int[] Di = new int[str2.length() + 1];

        for (int j = 0; j <= str2.length(); j++) {
            Di[j] = j; // (i == 0)
        }

        for (int i = 1; i <= str1.length(); i++) {
            System.arraycopy(Di, 0, Di_1, 0, Di_1.length);

            Di[0] = i; // (j == 0)
            for (int j = 1; j <= str2.length(); j++) {
                int cost = (str1.charAt(i - 1) != str2.charAt(j - 1)) ? 1 : 0;
                Di[j] = min(
                        Di_1[j] + 1,
                        Di[j - 1] + 1,
                        Di_1[j - 1] + cost
                );
            }
        }

        return Di[Di.length - 1];
    }
	
	private boolean isValidCollectionName(String name) {
		return name.matches("^[a-zA-Z]+$");
	}

    private static int min(int n1, int n2, int n3) {
        return Math.min(Math.min(n1, n2), n3);
    }


	public IUserClientReceiver getReceiverModule() {
		return receiverModule;
	}

	public void setReceiverModule(IUserClientReceiver receiverModule) {
		this.receiverModule = receiverModule;
	}

	public ILogicRequestFactory getLogicRequestFactory() {
		return logicRequestFactory;
	}

	public void setLogicRequestFactory(ILogicRequestFactory logicRequestFactory) {
		this.logicRequestFactory = logicRequestFactory;
	}
	
	public IUserClientInputModule getUserInputModule() {
		return userInputModule;
	}

	public void setUserInputModule(IUserClientInputModule userInputModule) {
		this.userInputModule = userInputModule;
	}

	public Stack<IScript> getScripts() {
		return scripts;
	}

	public void setScripts(Stack<IScript> scripts) {
		this.scripts = scripts;
	}

	public SocketAddress getSocketAddress() {
		return socketAddress;
	}

	public void setSocketAddress(SocketAddress socketAddress) {
		this.socketAddress = socketAddress;
	}

	public DatagramChannel getDatagramChannel() {
		return datagramChannel;
	}

	public void setDatagramChannel(DatagramChannel datagramChannel) {
		this.datagramChannel = datagramChannel;
	}


	public IUserClientRequestSender getRequestSenderModule() {
		return requestSenderModule;
	}


	public void setRequestSenderModule(IUserClientRequestSender requestSenderModule) {
		this.requestSenderModule = requestSenderModule;
	}

	@Override
	public String getStructureDescription() {
		return this.appName + " > Client" + ((this.userLogged) ? (" " + this.currentUserName) : "");
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getCurrentCollectionName() {
		return currentCollectionName;
	}

	public void setCurrentCollectionName(String currentCollectionName) {
		this.currentCollectionName = currentCollectionName;
	}

	public boolean isUserLogged() {
		return userLogged;
	}

	public void setUserLogged(boolean userLogged) {
		this.userLogged = userLogged;
	}

	public String getCurrentUserName() {
		return currentUserName;
	}

	public void setCurrentUserName(String currentUserName) {
		this.currentUserName = currentUserName;
	}

	public String getCurrentUserPassword() {
		return currentUserPassword;
	}

	public void setCurrentUserPassword(String currentUserPassword) {
		this.currentUserPassword = currentUserPassword;
	}

}
