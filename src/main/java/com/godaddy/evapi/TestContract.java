package com.godaddy.evapi;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.5.0.
 */
public class TestContract extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b506105d1806100206000396000f3006080604052600436106100615763ffffffff7c0100000000000000000000000000000000000000000000000000000000600035041663b319c9e48114610066578063be1c766b14610134578063c19083601461015b578063e16b4a9b14610206575b600080fd5b34801561007257600080fd5b506040805160206004803580820135601f81018490048402850184019095528484526100bf94369492936024939284019190819084018382808284375094975061021b9650505050505050565b6040805160208082528351818301528351919283929083019185019080838360005b838110156100f95781810151838201526020016100e1565b50505050905090810190601f1680156101265780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561014057600080fd5b50610149610392565b60408051918252519081900360200190f35b34801561016757600080fd5b506040805160206004803580820135601f81018490048402850184019095528484526101f294369492936024939284019190819084018382808284375050604080516020601f89358b018035918201839004830284018301909452808352979a9998810197919650918201945092508291508401838280828437509497506103999650505050505050565b604080519115158252519081900360200190f35b34801561021257600080fd5b506100bf6104d6565b60606001826040518082805190602001908083835b6020831061024f5780518252601f199092019160209182019101610230565b51815160209384036101000a600019018019909216911617905292019485525060405193849003019092206001015460ff1615915061037c9050576001826040518082805190602001908083835b602083106102bc5780518252601f19909201916020918201910161029d565b518151600019602094850361010090810a820192831692199390931691909117909252949092019687526040805197889003820188208054601f60026001831615909802909501169590950492830182900482028801820190528187529294509250508301828280156103705780601f1061034557610100808354040283529160200191610370565b820191906000526020600020905b81548152906001019060200180831161035357829003601f168201915b5050505050905061038d565b506040805160208101909152600081525b919050565b6000545b90565b6000806001846040518082805190602001908083835b602083106103ce5780518252601f1990920191602091820191016103af565b51815160209384036101000a600019018019909216911617905292019485525060405193849003019092206001015460ff16159150610412905057600091506104cf565b60025460ff161561042257600080fd5b6002805460ff191660019081179091556040518551869190819060208401908083835b602083106104645780518252601f199092019160209182019101610445565b51815160209384036101000a60001901801990921691161790529201948552506040519384900381019093206001808201805460ff1916909117905586519094506104b5938593508701915061050d565b506000805460019081019091556002805460ff1916905591505b5092915050565b60408051808201909152600f81527f54657374205375636365737366756c0000000000000000000000000000000000602082015290565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061054e57805160ff191683800117855561057b565b8280016001018555821561057b579182015b8281111561057b578251825591602001919060010190610560565b5061058792915061058b565b5090565b61039691905b8082111561058757600081556001016105915600a165627a7a72305820c07055cecb76da825093ed5b86150317a996e5297360de5332d77eeee7becff70029";

    public static final String FUNC_GETENTRY = "getEntry";

    public static final String FUNC_GETLENGTH = "getLength";

    public static final String FUNC_CREATEENTRY = "createEntry";

    public static final String FUNC_TESTFUNCTION = "testFunction";

    protected TestContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected TestContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<String> getEntry(String id) {
        final Function function = new Function(FUNC_GETENTRY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(id)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> getLength() {
        final Function function = new Function(FUNC_GETLENGTH, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> createEntry(String id, String name) {
        final Function function = new Function(
                FUNC_CREATEENTRY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(id), 
                new org.web3j.abi.datatypes.Utf8String(name)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> testFunction() {
        final Function function = new Function(FUNC_TESTFUNCTION, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public static RemoteCall<TestContract> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(TestContract.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<TestContract> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(TestContract.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static TestContract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new TestContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static TestContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new TestContract(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }
}
