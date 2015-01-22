package edu.rit.csci759.rspi.exercise;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import edu.rit.csci759.rspi.RpiIndicatorInterface;
import edu.rit.csci759.rspi.utils.MCP3008ADCReader;

public class RpiAmbientIntensityIndicatorDemo implements RpiIndicatorInterface {
	static final GpioController gpio = GpioFactory.getInstance();
	static final GpioPinDigitalOutput greenPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27, "green", PinState.LOW);
	static final GpioPinDigitalOutput yellowPin  = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28, "yellow",  PinState.LOW);
	static final GpioPinDigitalOutput redPin   = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_29, "red",   PinState.LOW);
	
	private static boolean keepRunning = true;
	
	public RpiAmbientIntensityIndicatorDemo(){	
	}
	
	
	@Override
	public void led_all_off() {
		redPin.low();
		greenPin.low();
		yellowPin.low();
	}

	@Override
	public void led_all_on() {
		redPin.high();
		greenPin.high();
		yellowPin.high();
	}

	@Override
	public void led_error(int blink_count) throws InterruptedException {
		int bc=0;
		while(bc<blink_count){
			redPin.high();
			Thread.sleep(500);
			redPin.low();
			Thread.sleep(500);
		}
	}

	@Override
	public void led_when_low() {
		redPin.high();
		greenPin.low();
		yellowPin.low();
	}

	@Override
	public void led_when_mid() {
		redPin.low();
		greenPin.low();
		yellowPin.high();
	}

	@Override
	public void led_when_high() {
		redPin.low();
		greenPin.high();
		yellowPin.low();
	}

	@Override
	public int read_ambient_light_intensity() {
		/*
		 * Reading ambient light from the photocell sensor using the MCP3008 ADC 
		 */
		int adc_ambient = MCP3008ADCReader.readAdc(MCP3008ADCReader.MCP3008_input_channels.CH1.ch());
		// [0, 1023] ~ [0x0000, 0x03FF] ~ [0&0, 0&1111111111]
		// convert in the range of 1-100
		return (int)(adc_ambient / 10.24); 
		
	}

	public static void main(String[] args) throws InterruptedException {
		RpiAmbientIntensityIndicatorDemo demo = new RpiAmbientIntensityIndicatorDemo();
		MCP3008ADCReader.initSPI(gpio);
		
		
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			public void run()
			{
				System.out.println("Shutting down.");
				keepRunning = false;
			}
		});
		
		int ambient=0;
		while (keepRunning)
		{
			ambient = demo.read_ambient_light_intensity();
	        
	        System.out.println("Ambient:" + ambient + "/100; ");
	        
	        if(ambient<=AMBIENT_DARK && ambient>=0)
	        	demo.led_when_low();
	        else if (ambient>AMBIENT_DARK && ambient<AMBIENT_BRIGHT)
	        	demo.led_when_mid();
	        else if(ambient>=AMBIENT_BRIGHT && ambient<=100)
	        	demo.led_when_high();
	        else
	        	demo.led_error(10);
	        
	       
			try { Thread.sleep(500L); } catch (InterruptedException ie) { ie.printStackTrace(); }
		}
		System.out.println("Bye...");
		gpio.shutdown();

	}


	@Override
	public int read_temperature() {
		// TODO Auto-generated method stub
		return 0;
	}

}
