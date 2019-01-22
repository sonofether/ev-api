pragma solidity ^0.4.7;
import "remix_tests.sol"; // this import is automatically injected by Remix.
import "./TestContract.sol";

contract testTestContract {
   
    TestContract testContract;
    function beforeAll () public {
        testContract = new TestContract();
    }
    
    function createAndGetItem() public {
        bool result = testContract.createEntry("id1", "asink.com");
        Assert.equal(result, true, "Create failed.");
        
        string memory name = testContract.getEntry("id1");
        Assert.equal(name, "asink.com", "Name is incorrect.");
    }
    
    function getLength() public {
        bool result = testContract.createEntry("id2", "asink2.com");
        Assert.equal(result, true, "Create failed.");
        uint256 len = testContract.getLength();
        Assert.equal(len > 0, true, "Length is 0.");
    }

    function test() public {
        string memory result = testContract.testFunction();
        Assert.equal(result, "Test Successful", "Test function failed");
    }
}