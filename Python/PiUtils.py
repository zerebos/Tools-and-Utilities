#!/usr/bin/env python
'''
 * Copyright (c) 2015 Zachary Rauen
 * Website: www.ZackRauen.com
 *
 * All rights reserved. Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * If a copy of the License is not provided with the work, you may
 * obtain a copy at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
'''
import smbus

class I2CException(Exception):
    
    def __init__(self,operation,reason):
        super(I2CException, self).__init__("Error with operation: "+operation,"Reason: "+reason)
    
    def __str__(self):
        return " ".join(self.args)
    
    def __repr__(self):
        return " ".join(self.args)

class BaseUtils(object):
    """Low level methods mainly used for Raspberry Pi"""
    
    @staticmethod
    def getRevision():
        """Uses the /proc/cpuinfo file to find the pi revision"""
        try:
            with open('/proc/cpuinfo','r') as infile:
                for line in infile:
                    if line.startswith('Revision'):
                        return 1 if line.rstrip()[-1] in ['0','2','3'] else 2
                    else:
                        return 0
        except:
            return 0
    
    @staticmethod
    def cleanBinaryData(data,length):
        try:
            data=bin(data)[2:]
        except TypeError:
            if data.find("0b")>=0:
                data=data[2:]
        return "0"*(length-len(data))+data
    
    @staticmethod
    def blankBinaryString(length,default='0'):
        return default*length
    
    @staticmethod
    def blankBinaryList(length,default='0'):
        return list(BaseUtils.blankBinaryString(length,default))
    
    @staticmethod
    def buildBinaryString(places,length,default='0'):
        blank = BaseUtils.blankBinaryList(length,default)
        for i in places: 
            if default=='0':
                blank[i]='1'
            else:
                blank[i]='0'
        return ''.join(blank)


class I2C(object):
    """
    This class is an easy-to-use wrapper for the SMBus library.
    It allows many I/O operations on I2C and SMBus lines.
    This was made for use with the raspberry-pi by default.
    """
    
    @staticmethod
    def probe(address, bus=False):
        """Probes a given address"""
        try:
            bus = smbus.SMBus(bus if bus else I2C.getI2CBus())
            if address>=0 and address<=255:
                bus.write_quick(address)
            else:
                raise I2CException("initialization","address is out of range (0-255)")
            return True
        except IOError:
            return False
        except I2CException as e:
            print(e)
            return False
    
    @staticmethod
    def getI2CBus():
        """Gets the default I2C bus number based on revision"""
        return 1 if BaseUtils.getRevision() > 1 else 0
        
    def __init__(self, address, bus=False, verbose=False):
        """
        Initializes the I2C class, a wrapper for the SMBus library
        
        @keyword arguments:
        @param address: address for I2C device
        @param bus: which I2C bus to access on the RPi
        @param verbose: allows all operations to output current actions.
        """
        try:
            if address>=0 and address<=255:
                self.address = address
            else:
                raise I2CException("initialization","address is out of range (0-255)")
            self.bus = smbus.SMBus(bus if bus else I2C.getI2CBus())
            self.verbose = verbose
        except I2CException as e:
            self.printError()
            print(e)
        
    def printError(self):
        """Prints the default error message and returns false"""
        print("Error with device 0x%02X, perhaps the address is wrong" % (self.address))
        return False
    
    def simpleReadByte(self, signed=False):
        """
        Reads a byte, signed or unsigned, via I2C
        
        @keyword arguments:
        @param signed: optional parameter, flips between signed and unsigned input
        """
        try:
            result = self.bus.read_byte(self.address)
            if result > 127 and signed: result -= 256
            if self.verbose:
                print("I2C: The device with address 0x%02X returned value 0x%02X" % (self.address, result))
                return result
        except IOError:
            return self.printError()
        except I2CException as e:
            print(e)
            return False;
        
    def readByte(self, register, signed=False):
        """
        Reads a byte, signed or unsigned, via I2C
        
        @keyword arguments:
        @param register: register address or command for I2C device
        @param signed: optional parameter, flips between signed and unsigned input
        """
        try:
            if register>=0 and register<=255:
                result = self.bus.read_byte_data(self.address, register)
            else:
                raise I2CException("reading","register address/command is out of range (0-255)")
            if result > 127 and signed: result -= 256
            if self.verbose:
                print("I2C: The device with address 0x%02X returned value 0x%02X from register/command 0x%02X"
                      % (self.address, result, register))
                return result
        except IOError:
            return self.printError()
        except I2CException as e:
            print(e)
            return False;
    
    def readWord(self, register, signed=False, bigEndian=False):
        """
        Reads a word (2 bytes), signed or unsigned, via I2C
        
        @keyword arguments:
        @param register: register address or command for I2C device
        @param signed: optional parameter, flips between signed and unsigned input
        @param bigEndian: optional parameter, allows switch from little endian system
        """
        try:
            if register>=0 and register<=255:
                result = self.bus.read_word_data(self.address, register)
            else:
                raise I2CException("reading","register address/command is out of range (0-255)")
            if bigEndian: result = ((result << 8) & 0xFF00) + (result >> 8)
            if result > 32767 and signed: result -= 65536
            if self.verbose:
                print("I2C: The device with address 0x%02X returned value 0x%04X from register/command 0x%02X"
                      % (self.address, result & 0xFFFF, register))
                return result
        except IOError:
            return self.printError()
        except I2CException as e:
            print(e)
            return False;

    def readListI2C(self, register, n):
        """
        Reads a list of unsigned bytes of length n
        
        @keyword arguments:
        @param register: register address or command for I2C device
        @param n: the number of bytes to accept
        """
        try:
            if n>32: Warning("This exceeds the capabilities of SMBus devices")
            if register>=0 and register<=255:
                results = self.bus.read_i2c_block_data(self.address, register, n)
            else:
                raise I2CException("reading","register address/command is out of range (0-255)")
            if self.verbose:
                print("I2C: The device with address 0x%02X returned the values below from register/command 0x%02X"
                      % (self.address, register))
                print(results)
                return results
        except IOError:
            return self.printError()
        except I2CException as e:
            print(e)
            return False;
        
    def readListSMBus(self, register, n):
        """
        Reads a list of unsigned bytes of length n
        
        @keyword arguments:
        @param register: register address or command for I2C device
        @param n: the number of bytes to accept
        """
        try:
            if n>32: raise Exception("This exceeds the capabilities of SMBus devices")
            if register>=0 and register<=255:
                results = self.bus.read_block_data(self.address, register, n)
            else:
                raise I2CException("reading","register address/command is out of range (0-255)")
            if self.verbose:
                print("I2C: The device with address 0x%02X returned the values below from register/command 0x%02X"
                      % (self.address, register))
                print(results)
                return results
        except IOError:
            return self.printError()
        except I2CException as e:
            print(e)
            return False;

    def simpleWriteByte(self, value):
        """
        Writes a given byte without subaddress/command byte
        
        @keyword arguments:
        @param register: register address or command for I2C device
        @param value: the value (byte) to be written to the device
        """
        try:
            if value>=0 and value<=255:
                self.bus.write_byte(self.address, value)
            else:
                raise I2CException("writing","value to write is out of range (0-255)")
            if self.verbose:
                print("I2C: Wrote value 0x%02X to device 0x%02X" % (value, self.address))
        except IOError:
            return self.printError()
        except I2CException as e:
            print(e)
            return False;

    def writeByte(self, register, value):
        """
        Writes a given byte after subaddress/command byte
        
        @keyword arguments:
        @param register: register address or command for I2C device
        @param value: the value (byte) to be written to the device
        """
        try:
            if register>=0 and register<=255:
                if value>=0 and value<=255:
                    self.bus.write_byte_data(self.address, register, value)
                else:
                    raise I2CException("writing","value to write is out of range (0-255)")
            else:
                raise I2CException("writing","register address/command is out of range (0-255)")
            if self.verbose:
                print("I2C: Wrote value 0x%02X to register/with command 0x%02X" % (value, register))
        except IOError:
            return self.printError()
        except I2CException as e:
            print(e)
            return False;
    
    def writeWord(self, register, value):
        """
        Writes a given word (2 bytes) after subaddress/command byte
        
        @keyword arguments:
        @param register: register address or command for I2C device
        @param value: the word (2 bytes) to be written to the device
        """
        try:
            if register>=0 and register<=255:
                if value>=0 and value<=255:
                    self.bus.write_word_data(self.address, register, value)
                else:
                    raise I2CException("writing","value to write is out of range (0-255)")
            else:
                raise I2CException("writing","register address/command is out of range (0-255)")
            if self.verbose:
                print("I2C: Wrote value 0x%04X to register pair 0x%02X,0x%02X or with command 0x%02X"
                      % (value, register, register+1,register))
        except IOError:
            return self.printError()
        except I2CException as e:
            print(e)
            return False;

    def writeListI2C(self, register, data):
        """
        Writes a given set of bytes after subaddress/command byte
        
        @keyword arguments:
        @param register: register address or command for I2C device
        @param data: the list of bytes to be written out
        """
        try:
            if self.debug:
                print("I2C: Writing data to register/with command 0x%02X:" % register)
                print(data)
            if len(data)>32: Warning("This exceeds the capabilities of SMBus devices")
            if register>=0 and register<=255:
                self.bus.write_i2c_block_data(self.address, register, data)
            else:
                raise I2CException("reading","register address/command is out of range (0-255)")
        except IOError:
            return self.errMsg()
        except I2CException as e:
            print(e)
            return False;
        
    def writeListSMBus(self, register, data):
        """
        Writes a given set of bytes after subaddress/command byte
        
        @keyword arguments:
        @param register: register address or command for I2C device
        @param data: the list of bytes to be written out
        """
        try:
            if self.debug:
                print("I2C: Writing data to register/with command 0x%02X:" % register)
                print(data)
            if len(data)>32: raise Exception("This exceeds the capabilities of SMBus devices")
            if register>=0 and register<=255:
                self.bus.write_block_data(self.address, register, data)
            else:
                raise I2CException("reading","register address/command is out of range (0-255)")
        except IOError:
            return self.errMsg()
        except I2CException as e:
            print(e)
            return False;

    
def probeAll():
    i2cList = list()
    for i in range(0,256):
        print("Probing 0x%02X"%i)
        tester=I2C.probe(i)
        if tester:
            i2cList.append(i)
        print("Found a total of "+str(len(i2cList))+" i2c devices connected!")
        print(i2cList)
        
if __name__ == '__main__':
    probeAll();
