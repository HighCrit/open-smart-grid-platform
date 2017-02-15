Feature: BasicOsgpFunctions Protocol Sequence Number
  As a ...
  I want to ...
  In order to ...

  @OslpMockServer
  Scenario Outline: Valid sequence number ranges
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
    And the device returns a start device response "OK" over OSLP
    And the device adds "<AddNumberToSequenceNumber>" to the sequencenumber in the OSLP response
    When receiving a start device test request
      | DeviceIdentification | TEST1024000000001 |
    Then the start device async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a start device OSLP message is sent to device "TEST1024000000001"
    And the platform buffers a start device response message for device "TEST1024000000001"
      | Result | OK |

    # Note: In the file 'DeviceRegistrationService' is a check which doesn't accept numbers below and equal to '0'. When this happens the result is always false.
    Examples: 
      | AddNumberToSequenceNumber |
      |                         0 |
      |                         1 |
      |                         2 |
      |                         3 |
      |                         4 |
      |                         5 |
      |                         6 |

  @OslpMockServer
  Scenario Outline: Invalid sequence number ranges
    Given an ssld oslp device
      | DeviceIdentification | TEST1024000000001 |
    And the device returns a start device response "OK" over OSLP
    And the device adds "<AddNumberToSequenceNumber>" to the sequencenumber in the OSLP response
    When receiving a start device test request
      | DeviceIdentification | TEST1024000000001 |
    Then the start device async response contains
      | DeviceIdentification | TEST1024000000001 |
    And a start device OSLP message is sent to device "TEST1024000000001"
    And the platform buffers no start device test response message for device "TEST1024000000001"

    # Note: In the file 'DeviceRegistrationService' is a check which doesn't accept numbers below and equal to '0'. When this happens the result is always false.
    Examples: 
      | AddNumberToSequenceNumber |
      |                        -8 |
      |                        -7 |
      |                         7 |
      |                         8 |
