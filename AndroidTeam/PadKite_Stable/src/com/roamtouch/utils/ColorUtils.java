package com.roamtouch.utils;

import java.util.Hashtable;

public class ColorUtils {
	
	public static int[] L1_Color(int[] colors){		
		
    	int[] colorArray = new int[3];    	
    	
    	/**INPUT**/
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
    		  COLOR.PANTONE_192C_MAIN[0] 
    	    + COLOR.PANTONE_192C_MAIN[1] 
    	    + COLOR.PANTONE_192C_MAIN[2])){    		
    		colorArray = (int[]) COLOR.PANTONE_189C_L1;
    	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_YellowC_MAIN[0] 
      	    + COLOR.PANTONE_YellowC_MAIN[1] 
      	    + COLOR.PANTONE_YellowC_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_100C_L1;
      	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
        		  COLOR.PANTONE_246C_MAIN[0] 
        	    + COLOR.PANTONE_246C_MAIN[1] 
        	    + COLOR.PANTONE_246C_MAIN[2])){    		
        		colorArray = (int[]) COLOR.PANTONE_243C_L1;
        } 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_631C_MAIN[0] 
      	    + COLOR.PANTONE_631C_MAIN[1] 
      	    + COLOR.PANTONE_631C_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_628C_L1;
    	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
        		  COLOR.PANTONE_444C_MAIN[0] 
        	    + COLOR.PANTONE_444C_MAIN[1] 
        	    + COLOR.PANTONE_444C_MAIN[2])){    		
        		colorArray = (int[]) COLOR.PANTONE_441C_L1;
      	}   	
    	
    	/**PANEL**/    	
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_375C_MAIN[0] 
      	    + COLOR.PANTONE_375C_MAIN[1] 
      	    + COLOR.PANTONE_375C_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_372C_L1;
    	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
        		  COLOR.PANTONE_130C_MAIN[0] 
        	    + COLOR.PANTONE_130C_MAIN[1] 
        	    + COLOR.PANTONE_130C_MAIN[2])){    		
        		colorArray = (int[]) COLOR.PANTONE_127C_L1;
      	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_2736C_MAIN[0] 
      	    + COLOR.PANTONE_2736C_MAIN[1] 
      	    + COLOR.PANTONE_2736C_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_2706C_L1;
    	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
        		  COLOR.PANTONE_ProcessBlueC_MAIN[0] 
        	    + COLOR.PANTONE_ProcessBlueC_MAIN[1] 
        	    + COLOR.PANTONE_ProcessBlueC_MAIN[2])){    		
        		colorArray = (int[]) COLOR.PANTONE_304C_L1;
      	} 
    	
    	/**COMMON**/
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_444C_MAIN[0] 
      	    + COLOR.PANTONE_444C_MAIN[1] 
      	    + COLOR.PANTONE_444C_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_441C_L1;
    	} 
    	
    	/**ANCHOR**/
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
        		  COLOR.PANTONE_340C_MAIN[0] 
        	    + COLOR.PANTONE_340C_MAIN[1] 
        	    + COLOR.PANTONE_340C_MAIN[2])){    		
        		colorArray = (int[]) COLOR.PANTONE_337C_L1;
      	} 
    	
		return colorArray;		
    }

	public static int[] L2_Color(int[] colors){		
    	int[] colorArray = new int[3];    
    	
    	/**INPUT**/
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
    		  COLOR.PANTONE_192C_MAIN[0] 
    	    + COLOR.PANTONE_192C_MAIN[1] 
    	    + COLOR.PANTONE_192C_MAIN[2])){    		
    		colorArray = (int[]) COLOR.PANTONE_190C_L2;
    	}
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
        		  COLOR.PANTONE_YellowC_MAIN[0] 
        	    + COLOR.PANTONE_YellowC_MAIN[1] 
        	    + COLOR.PANTONE_YellowC_MAIN[2])){    		
        		colorArray = (int[]) COLOR.PANTONE_101C_L2;
        } 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_246C_MAIN[0] 
      	    + COLOR.PANTONE_246C_MAIN[1] 
      	    + COLOR.PANTONE_246C_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_244C_L2;
    	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
        		  COLOR.PANTONE_631C_MAIN[0] 
        	    + COLOR.PANTONE_631C_MAIN[1] 
        	    + COLOR.PANTONE_631C_MAIN[2])){    		
        		colorArray = (int[]) COLOR.PANTONE_628C_L1;
      	} 
    	
    	/**PANEL**/    	
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_375C_MAIN[0] 
      	    + COLOR.PANTONE_375C_MAIN[1] 
      	    + COLOR.PANTONE_375C_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_373C_L2;
    	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_130C_MAIN[0] 
      	    + COLOR.PANTONE_130C_MAIN[1] 
      	    + COLOR.PANTONE_130C_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_128C_L2;
    	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
        		  COLOR.PANTONE_2736C_MAIN[0] 
        	    + COLOR.PANTONE_2736C_MAIN[1] 
        	    + COLOR.PANTONE_2736C_MAIN[2])){    		
        		colorArray = (int[]) COLOR.PANTONE_2716C_L2;
      	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_ProcessBlueC_MAIN[0] 
      	    + COLOR.PANTONE_ProcessBlueC_MAIN[1] 
      	    + COLOR.PANTONE_ProcessBlueC_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_305C_L2;
    	} 
    	
    	/**COMMON**/ 	    	
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_444C_MAIN[0] 
      	    + COLOR.PANTONE_444C_MAIN[1] 
      	    + COLOR.PANTONE_444C_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_442C_L2;
    	} 
    	
    	/**ANCHOR**/ 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
        		  COLOR.PANTONE_340C_MAIN[0] 
        	    + COLOR.PANTONE_340C_MAIN[1] 
        	    + COLOR.PANTONE_340C_MAIN[2])){    		
        		colorArray = (int[]) COLOR.PANTONE_338C_L2;
      	} 
    	
		return colorArray;		
    }
	
	public static int[] L3_Color(int[] colors){		
    	int[] colorArray = new int[3];   
    	
    	/**INPUT**/
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
    		  COLOR.PANTONE_192C_MAIN[0] 
    	    + COLOR.PANTONE_192C_MAIN[1] 
    	    + COLOR.PANTONE_192C_MAIN[2])){    		
    		colorArray = (int[]) COLOR.PANTONE_191C_L3;
    	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_YellowC_MAIN[0] 
      	    + COLOR.PANTONE_YellowC_MAIN[1] 
      	    + COLOR.PANTONE_YellowC_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_102C_L3;
    	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_246C_MAIN[0] 
      	    + COLOR.PANTONE_246C_MAIN[1] 
      	    + COLOR.PANTONE_246C_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_245C_L3;
    	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
        		  COLOR.PANTONE_631C_MAIN[0] 
        	    + COLOR.PANTONE_631C_MAIN[1] 
        	    + COLOR.PANTONE_631C_MAIN[2])){    		
        		colorArray = (int[]) COLOR.PANTONE_630C_L3;
      	} 
    	
    	/**PANEL**/    	
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_375C_MAIN[0] 
      	    + COLOR.PANTONE_375C_MAIN[1] 
      	    + COLOR.PANTONE_375C_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_375C_L3;
    	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_130C_MAIN[0] 
      	    + COLOR.PANTONE_130C_MAIN[1] 
      	    + COLOR.PANTONE_130C_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_129C_L3;
    	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
        		  COLOR.PANTONE_2736C_MAIN[0] 
        	    + COLOR.PANTONE_2736C_MAIN[1] 
        	    + COLOR.PANTONE_2736C_MAIN[2])){    		
        		colorArray = (int[]) COLOR.PANTONE_2726C_L3;
      	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_ProcessBlueC_MAIN[0] 
      	    + COLOR.PANTONE_ProcessBlueC_MAIN[1] 
      	    + COLOR.PANTONE_ProcessBlueC_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_306C_L3;
    	} 
    	
    	/**COMMON**/
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_444C_MAIN[0] 
      	    + COLOR.PANTONE_444C_MAIN[1] 
      	    + COLOR.PANTONE_444C_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_443C_L3;
    	} 
    	
    	/**ANCHOR**/ 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
        		  COLOR.PANTONE_340C_MAIN[0] 
        	    + COLOR.PANTONE_340C_MAIN[1] 
        	    + COLOR.PANTONE_340C_MAIN[2])){    		
        		colorArray = (int[]) COLOR.PANTONE_339C_L3;
      	} 
    	
		return colorArray;		
    }
	
	public static int[] D3_Color(int[] colors){		
    	
		int[] colorArray = new int[3];    	
		
		/**INPUT**/
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
    		  COLOR.PANTONE_192C_MAIN[0] 
    	    + COLOR.PANTONE_192C_MAIN[1] 
    	    + COLOR.PANTONE_192C_MAIN[2])){    		
    		colorArray = (int[]) COLOR.PANTONE_193C_D3;
    	
    	}   	
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
        		  COLOR.PANTONE_YellowC_MAIN[0] 
        	    + COLOR.PANTONE_YellowC_MAIN[1] 
        	    + COLOR.PANTONE_YellowC_MAIN[2])){    		
        		colorArray = (int[]) COLOR.PANTONE_103C_D3;
      	}
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_246C_MAIN[0] 
      	    + COLOR.PANTONE_246C_MAIN[1] 
      	    + COLOR.PANTONE_246C_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_247C_D3;
    	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
        		  COLOR.PANTONE_631C_MAIN[0] 
        	    + COLOR.PANTONE_631C_MAIN[1] 
        	    + COLOR.PANTONE_631C_MAIN[2])){    		
        		colorArray = (int[]) COLOR.PANTONE_632C_D3;
      	} 
    	
    	/**PANEL**/    	
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_375C_MAIN[0] 
      	    + COLOR.PANTONE_375C_MAIN[1] 
      	    + COLOR.PANTONE_375C_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_376C_D3;
    	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_130C_MAIN[0] 
      	    + COLOR.PANTONE_130C_MAIN[1] 
      	    + COLOR.PANTONE_130C_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_131C_D3;
    	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
        		  COLOR.PANTONE_2736C_MAIN[0] 
        	    + COLOR.PANTONE_2736C_MAIN[1] 
        	    + COLOR.PANTONE_2736C_MAIN[2])){    		
        		colorArray = (int[]) COLOR.PANTONE_2746C_D3;
      	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_ProcessBlueC_MAIN[0] 
      	    + COLOR.PANTONE_ProcessBlueC_MAIN[1] 
      	    + COLOR.PANTONE_ProcessBlueC_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_307C_D3;
    	} 
    	
    	/**COMMON**/
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_444C_MAIN[0] 
      	    + COLOR.PANTONE_444C_MAIN[1] 
      	    + COLOR.PANTONE_444C_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_445C_D3;
    	}    
    	
    	/**ANCHOR**/ 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
        		  COLOR.PANTONE_340C_MAIN[0] 
        	    + COLOR.PANTONE_340C_MAIN[1] 
        	    + COLOR.PANTONE_340C_MAIN[2])){    		
        		colorArray = (int[]) COLOR.PANTONE_341C_D3;
      	} 
    	
		return colorArray;		
    }
	
	public static int[] D2_Color(int[] colors){	
		
    	int[] colorArray = new int[3];  
    	
    	/**INPUT**/
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
    		  COLOR.PANTONE_192C_MAIN[0] 
    	    + COLOR.PANTONE_192C_MAIN[1] 
    	    + COLOR.PANTONE_192C_MAIN[2])){    		
    		colorArray = (int[]) COLOR.PANTONE_194C_D2;
    	}
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
        		  COLOR.PANTONE_YellowC_MAIN[0] 
        	    + COLOR.PANTONE_YellowC_MAIN[1] 
        	    + COLOR.PANTONE_YellowC_MAIN[2])){    		
        		colorArray = (int[]) COLOR.PANTONE_104C_D2;
      	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_246C_MAIN[0] 
      	    + COLOR.PANTONE_246C_MAIN[1] 
      	    + COLOR.PANTONE_246C_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_248C_D2;
    	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
        		  COLOR.PANTONE_631C_MAIN[0] 
        	    + COLOR.PANTONE_631C_MAIN[1] 
        	    + COLOR.PANTONE_631C_MAIN[2])){    		
        		colorArray = (int[]) COLOR.PANTONE_633C_D2;
      	} 
    	
    	/**PANEL**/    	
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_375C_MAIN[0] 
      	    + COLOR.PANTONE_375C_MAIN[1] 
      	    + COLOR.PANTONE_375C_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_377C_D2;
    	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_130C_MAIN[0] 
      	    + COLOR.PANTONE_130C_MAIN[1] 
      	    + COLOR.PANTONE_130C_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_132C_D2;
    	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
        		  COLOR.PANTONE_2736C_MAIN[0] 
        	    + COLOR.PANTONE_2736C_MAIN[1] 
        	    + COLOR.PANTONE_2736C_MAIN[2])){    		
        		colorArray = (int[]) COLOR.PANTONE_2756C_D2;
      	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_ProcessBlueC_MAIN[0] 
      	    + COLOR.PANTONE_ProcessBlueC_MAIN[1] 
      	    + COLOR.PANTONE_ProcessBlueC_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_308C_D2;
    	} 
    	
    	/**COMMON**/
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_444C_MAIN[0] 
      	    + COLOR.PANTONE_444C_MAIN[1] 
      	    + COLOR.PANTONE_444C_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_446C_D2;
    	} 
    	
    	/**ANCHOR**/ 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
        		  COLOR.PANTONE_340C_MAIN[0] 
        	    + COLOR.PANTONE_340C_MAIN[1] 
        	    + COLOR.PANTONE_340C_MAIN[2])){    		
        		colorArray = (int[]) COLOR.PANTONE_342C_D2;
      	} 
    	
		return colorArray;		
    }
	
	public static int[] D1_Color(int[] colors){		
		
    	int[] colorArray = new int[3];    	
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
    		  COLOR.PANTONE_192C_MAIN[0] 
    	    + COLOR.PANTONE_192C_MAIN[1] 
    	    + COLOR.PANTONE_192C_MAIN[2])){    		
    		colorArray = (int[]) COLOR.PANTONE_195C_D1;
    	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_YellowC_MAIN[0] 
      	    + COLOR.PANTONE_YellowC_MAIN[1] 
      	    + COLOR.PANTONE_YellowC_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_105C_D1;
    	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_246C_MAIN[0] 
      	    + COLOR.PANTONE_246C_MAIN[1] 
      	    + COLOR.PANTONE_246C_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_249C_D1;
    	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
        		  COLOR.PANTONE_631C_MAIN[0] 
        	    + COLOR.PANTONE_631C_MAIN[1] 
        	    + COLOR.PANTONE_631C_MAIN[2])){    		
        		colorArray = (int[]) COLOR.PANTONE_634C_D1;
      	} 
    	
    	/**PANEL**/    	
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_375C_MAIN[0] 
      	    + COLOR.PANTONE_375C_MAIN[1] 
      	    + COLOR.PANTONE_375C_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_378C_D1;
    	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_130C_MAIN[0] 
      	    + COLOR.PANTONE_130C_MAIN[1] 
      	    + COLOR.PANTONE_130C_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_133C_D1;
    	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
        		  COLOR.PANTONE_2736C_MAIN[0] 
        	    + COLOR.PANTONE_2736C_MAIN[1] 
        	    + COLOR.PANTONE_2736C_MAIN[2])){    		
        		colorArray = (int[]) COLOR.PANTONE_2766C_D1;
      	} 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_ProcessBlueC_MAIN[0] 
      	    + COLOR.PANTONE_ProcessBlueC_MAIN[1] 
      	    + COLOR.PANTONE_ProcessBlueC_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_309C_D1;
    	} 
    	
    	/**COMMON**/
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
      		  COLOR.PANTONE_444C_MAIN[0] 
      	    + COLOR.PANTONE_444C_MAIN[1] 
      	    + COLOR.PANTONE_444C_MAIN[2])){    		
      		colorArray = (int[]) COLOR.PANTONE_447C_D1;
    	} 
    	
    	/**ANCHOR**/ 
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (
        		  COLOR.PANTONE_340C_MAIN[0] 
        	    + COLOR.PANTONE_340C_MAIN[1] 
        	    + COLOR.PANTONE_340C_MAIN[2])){    		
        		colorArray = (int[]) COLOR.PANTONE_343C_D1;
      	} 
    	
		return colorArray;		
    }
	
	

	public static int[] checkDarkColor(int[] colors){
    	
    	int[] colorArray = new int[3];
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.VIOLET[0] + COLOR.VIOLET[1] + COLOR.VIOLET[2])){
    		colorArray = (int[]) COLOR.VIOLET_DARK;
    	}
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.ORANGE[0] + COLOR.ORANGE[1] + COLOR.ORANGE[2])){
    		colorArray = (int[]) COLOR.ORANGE_DARK;
    	}
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.LIGTH_BLUE[0] + COLOR.LIGTH_BLUE[1] + COLOR.LIGTH_BLUE[2])){
    		colorArray = (int[]) COLOR.LIGTH_BLUE_DARK;
    	}
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.RED[0] + COLOR.RED[1] + COLOR.RED[2])){
    		colorArray = (int[]) COLOR.RED_DARK;
    	}
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.YELLOW[0] + COLOR.YELLOW[1] + COLOR.YELLOW[2])){
    		colorArray = (int[]) COLOR.YELLOW_DARK;
    	}
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.GREEN[0] + COLOR.GREEN[1] + COLOR.GREEN[2])){
    		colorArray = (int[]) COLOR.GREEN_DARK;
    	}
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.BLUE[0] + COLOR.BLUE[1] + COLOR.BLUE[2])){
    		colorArray = (int[]) COLOR.BLUE_DARK;
    	}
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.BLACK[0] + COLOR.BLACK[1] + COLOR.BLACK[2])){
    		colorArray = (int[]) COLOR.BLACK_DARK;
    	} 
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.RED_MAP[0] + COLOR.RED_MAP[1] + COLOR.RED_MAP[2])){
    		colorArray = (int[]) COLOR.RED_MAP_DARK;
    	} 
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.TURQUOISE[0] + COLOR.TURQUOISE[1] + COLOR.TURQUOISE[2])){
    		colorArray = (int[]) COLOR.TURQUOISE_DARK;
    	} 
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.GRAY[0] + COLOR.GRAY[1] + COLOR.GRAY[2])){
    		colorArray = (int[]) COLOR.GRAY_DARK;
    	} 
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.FUXIA[0] + COLOR.FUXIA[1] + COLOR.FUXIA[2])){
    		colorArray = (int[]) COLOR.FUXIA_DARK;
    	} 
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.APPLE[0] + COLOR.APPLE[1] + COLOR.APPLE[2])){
    		colorArray = (int[]) COLOR.APPLE_DARK;
    	} 
		return colorArray;
    }
	
	
	public static int[] checkLightColor(int[] colors){
    	
    	int[] colorArray = new int[3];
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.VIOLET[0] + COLOR.VIOLET[1] + COLOR.VIOLET[2])){
    		colorArray = (int[]) COLOR.VIOLET_LIGHT;
    	}
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.ORANGE[0] + COLOR.ORANGE[1] + COLOR.ORANGE[2])){
    		colorArray = (int[]) COLOR.ORANGE_LIGHT;
    	}
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.LIGTH_BLUE[0] + COLOR.LIGTH_BLUE[1] + COLOR.LIGTH_BLUE[2])){
    		colorArray = (int[]) COLOR.LIGTH_BLUE_LIGHT;
    	}
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.RED[0] + COLOR.RED[1] + COLOR.RED[2])){
    		colorArray = (int[]) COLOR.RED_LIGHT;
    	}
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.YELLOW[0] + COLOR.YELLOW[1] + COLOR.YELLOW[2])){
    		colorArray = (int[]) COLOR.YELLOW_LIGHT;
    	}
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.GREEN[0] + COLOR.GREEN[1] + COLOR.GREEN[2])){
    		colorArray = (int[]) COLOR.GREEN_LIGHT;
    	}
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.BLUE[0] + COLOR.BLUE[1] + COLOR.BLUE[2])){
    		colorArray = (int[]) COLOR.BLUE_LIGHT;
    	}
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.BLACK[0] + COLOR.BLACK[1] + COLOR.BLACK[2])){
    		colorArray = (int[]) COLOR.BLACK_LIGHT;
    	} 
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.RED_MAP[0] + COLOR.RED_MAP[1] + COLOR.RED_MAP[2])){
    		colorArray = (int[]) COLOR.RED_MAP_LIGHT;
    	} 
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.TURQUOISE[0] + COLOR.TURQUOISE[1] + COLOR.TURQUOISE[2])){
    		colorArray = (int[]) COLOR.TURQUOISE_LIGHT;
    	} 
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.GRAY[0] + COLOR.GRAY[1] + COLOR.GRAY[2])){
    		colorArray = (int[]) COLOR.LIGHT_GRAY_LIGHT;
    	} 
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.FUXIA[0] + COLOR.FUXIA[1] + COLOR.FUXIA[2])){
    		colorArray = (int[]) COLOR.FUXIA_LIGHT;
    	}
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.APPLE[0] + COLOR.APPLE[1] + COLOR.APPLE[2])){
    		colorArray = (int[]) COLOR.APPLE_LIGHT;
    	}
		return colorArray;
    }
	
	
	public static int[] checkVeryLightColor(int[] colors){
    	
    	int[] colorArray = new int[3];
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.VIOLET[0] + COLOR.VIOLET[1] + COLOR.VIOLET[2])){
    		colorArray = (int[]) COLOR.VIOLET_VERY_LIGHT;
    	}
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.ORANGE[0] + COLOR.ORANGE[1] + COLOR.ORANGE[2])){
    		colorArray = (int[]) COLOR.ORANGE_VERY_LIGHT;
    	}
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.LIGTH_BLUE[0] + COLOR.LIGTH_BLUE[1] + COLOR.LIGTH_BLUE[2])){
    		colorArray = (int[]) COLOR.LIGTH_BLUE_VERY_LIGHT;
    	}
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.RED[0] + COLOR.RED[1] + COLOR.RED[2])){
    		colorArray = (int[]) COLOR.RED_VERY_LIGHT;
    	}
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.YELLOW[0] + COLOR.YELLOW[1] + COLOR.YELLOW[2])){
    		colorArray = (int[]) COLOR.YELLOW_VERY_LIGHT;
    	}
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.GREEN[0] + COLOR.GREEN[1] + COLOR.GREEN[2])){
    		colorArray = (int[]) COLOR.GREEN_VERY_LIGHT;
    	}
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.BLUE[0] + COLOR.BLUE[1] + COLOR.BLUE[2])){
    		colorArray = (int[]) COLOR.BLUE_VERY_LIGHT;
    	}
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.BLACK[0] + COLOR.BLACK[1] + COLOR.BLACK[2])){
    		colorArray = (int[]) COLOR.BLACK_VERY_LIGHT;
    	} 
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.RED_MAP[0] + COLOR.RED_MAP[1] + COLOR.RED_MAP[2])){
    		colorArray = (int[]) COLOR.RED_MAP_VERY_LIGHT;
    	} 
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.TURQUOISE[0] + COLOR.TURQUOISE[1] + COLOR.TURQUOISE[2])){
    		colorArray = (int[]) COLOR.TURQUOISE_VERY_LIGHT;
    	} 
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.GRAY[0] + COLOR.GRAY[1] + COLOR.GRAY[2])){
    		colorArray = (int[]) COLOR.LIGHT_GRAY_VERY_LIGHT;
    	} 
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.FUXIA[0] + COLOR.FUXIA[1] + COLOR.FUXIA[2])){
    		colorArray = (int[]) COLOR.FUXIA_VERY_LIGHT;
    	}
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.APPLE[0] + COLOR.APPLE[1] + COLOR.APPLE[2])){
    		colorArray = (int[]) COLOR.APPLE_VERY_LIGHT;
    	}
		return colorArray;
    }

	public static int[] checkVeryDarkColor(int[] colors){
    	
    	int[] colorArray = new int[3];
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.VIOLET[0] + COLOR.VIOLET[1] + COLOR.VIOLET[2])){
    		colorArray = (int[]) COLOR.VIOLET_VERY_DARK;
    	}
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.ORANGE[0] + COLOR.ORANGE[1] + COLOR.ORANGE[2])){
    		colorArray = (int[]) COLOR.ORANGE_VERY_DARK;
    	}
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.LIGTH_BLUE[0] + COLOR.LIGTH_BLUE[1] + COLOR.LIGTH_BLUE[2])){
    		colorArray = (int[]) COLOR.LIGTH_BLUE_VERY_DARK;
    	}
    	
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.RED[0] + COLOR.RED[1] + COLOR.RED[2])){
    		colorArray = (int[]) COLOR.RED_VERY_DARK;
    	}
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.YELLOW[0] + COLOR.YELLOW[1] + COLOR.YELLOW[2])){
    		colorArray = (int[]) COLOR.YELLOW_VERY_DARK;
    	}
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.GREEN[0] + COLOR.GREEN[1] + COLOR.GREEN[2])){
    		colorArray = (int[]) COLOR.GREEN_VERY_DARK;
    	}
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.BLUE[0] + COLOR.BLUE[1] + COLOR.BLUE[2])){
    		colorArray = (int[]) COLOR.BLUE_VERY_DARK;
    	}
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.BLACK[0] + COLOR.BLACK[1] + COLOR.BLACK[2])){
    		colorArray = (int[]) COLOR.BLACK_VERY_DARK;
    	} 
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.RED_MAP[0] + COLOR.RED_MAP[1] + COLOR.RED_MAP[2])){
    		colorArray = (int[]) COLOR.RED_MAP_VERY_DARK;
    	} 
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.TURQUOISE[0] + COLOR.TURQUOISE[1] + COLOR.TURQUOISE[2])){
    		colorArray = (int[]) COLOR.TURQUOISE_VERY_DARK;
    	} 
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.GRAY[0] + COLOR.GRAY[1] + COLOR.GRAY[2])){
    		colorArray = (int[]) COLOR.LIGHT_GRAY_VERY_DARK;
    	} 
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.FUXIA[0] + COLOR.FUXIA[1] + COLOR.FUXIA[2])){
    		colorArray = (int[]) COLOR.FUXIA_VERY_DARK;
    	} 
    	if ( (colors[0]+colors[1]+colors[2]) == (COLOR.APPLE[0] + COLOR.APPLE[1] + COLOR.APPLE[2])){
    		colorArray = (int[]) COLOR.APPLE_VERY_DARK;
    	} 
		return colorArray;
    }
	
	public static Hashtable<Integer, int[]> setSpinnerColors(Hashtable<Integer, int[]> colorSpinnerDots){	
		
		colorSpinnerDots.put(0, COLOR.CIRCLE_PANTONE_801C);
		colorSpinnerDots.put(1, COLOR.CIRCLE_PANTONE_802C);
		colorSpinnerDots.put(2, COLOR.CIRCLE_PANTONE_803C);
		colorSpinnerDots.put(3, COLOR.CIRCLE_PANTONE_804C);
		colorSpinnerDots.put(4, COLOR.CIRCLE_PANTONE_805C);
		colorSpinnerDots.put(5, COLOR.CIRCLE_PANTONE_806C);
		colorSpinnerDots.put(6, COLOR.CIRCLE_PANTONE_807C);
		colorSpinnerDots.put(7, COLOR.CIRCLE_PANTONE_808C);
		colorSpinnerDots.put(8, COLOR.CIRCLE_PANTONE_809C);
	    colorSpinnerDots.put(9, COLOR.CIRCLE_PANTONE_810C);     
	    colorSpinnerDots.put(10, COLOR.CIRCLE_PANTONE_811C);
	    colorSpinnerDots.put(11, COLOR.CIRCLE_PANTONE_812C);
	    colorSpinnerDots.put(12, COLOR.CIRCLE_PANTONE_813C);
	    colorSpinnerDots.put(13, COLOR.CIRCLE_PANTONE_814C);  
	    colorSpinnerDots.put(14, COLOR.CIRCLE_PANTONE_801C);
	    colorSpinnerDots.put(15, COLOR.CIRCLE_PANTONE_802C);
	    colorSpinnerDots.put(16, COLOR.CIRCLE_PANTONE_803C);
	    colorSpinnerDots.put(17, COLOR.CIRCLE_PANTONE_804C);
		
		return colorSpinnerDots;
		
	}
	
}


//INUPUT		
/*COLOR.PANTONE_192C_MAIN,
COLOR.PANTONE_YellowC_MAIN, 
COLOR.PANTONE_246C_MAIN,
COLOR.PANTONE_631C_MAIN, 
COLOR.PANTONE_444C_MAIN };*/

/*public static int[] PANTONE_189C_L1			= {248, 164, 187};
public static int[] PANTONE_190C_L2			= {248, 121, 155};
public static int[] PANTONE_191C_L3			= {241, 68, 111};
public static int[] PANTONE_192C_MAIN		= {231, 13, 71};
public static int[] PANTONE_193C_D3			= {192, 20, 60};
public static int[] PANTONE_194C_D2			= {155, 36, 62};
public static int[] PANTONE_195C_D1			= {121, 49, 65};*/	

/*public static int[] PANTONE_100C_L1				= {243, 235, 123};
public static int[] PANTONE_101C_L2				= {246, 236, 90};	
public static int[] PANTONE_102C_L3				= {251, 231, 0};
public static int[] PANTONE_YellowC_MAIN		= {254, 224, 0};
public static int[] PANTONE_103C_D3				= {199, 172, 0};
public static int[] PANTONE_104C_D2				= {175, 154, 0};
public static int[] PANTONE_105C_D1				= {135, 121, 36};*/
	
/*// 246C		
public static int[] PANTONE_243C_L1				= {239, 198, 227};
public static int[] PANTONE_244C_L2				= {233, 159, 219};	
public static int[] PANTONE_245C_L3				= {226, 130, 210};
public static int[] PANTONE_246C_MAIN			= {200, 33, 172};
public static int[] PANTONE_247C_D3				= {183, 11, 155};
public static int[] PANTONE_248C_D2				= {159, 24, 136};
public static int[] PANTONE_249C_D1				= {120, 41, 100};*/

/*// 631C
public static int[] PANTONE_628C_L1				= {192, 226, 230};
public static int[] PANTONE_629C_L2				= {159, 215, 225};	
public static int[] PANTONE_630C_L3				= {129, 204, 221};
public static int[] PANTONE_631C_MAIN			= {52, 181, 208};
public static int[] PANTONE_632C_D3				= {0, 154, 188};
public static int[] PANTONE_633C_D2				= {0, 125, 164};
public static int[] PANTONE_634C_D1				= {0, 102, 144};*/

// 444C
/*public static int[] PANTONE_441C_L1				= {188, 197, 193};
public static int[] PANTONE_442C_L2				= {167, 178, 177};	
public static int[] PANTONE_443C_L3				= {146, 157, 158};
public static int[] PANTONE_444C_MAIN			= {113, 127, 129};
public static int[] PANTONE_445C_D3				= {75, 84, 87};
public static int[] PANTONE_446C_D2				= {62, 69, 69};
public static int[] PANTONE_447C_D1				= {52, 55, 53};*/

/*		
COLOR.PANTONE_375C_MAIN,				
COLOR.PANTONE_130C_MAIN, 
COLOR.PANTONE_2736C_MAIN,
COLOR.PANTONE_ProcessBlueC_MAIN, */

/*// 375C		
public static int[] PANTONE_372C_L1			= {215, 235, 156};
public static int[] PANTONE_373C_L2			= {207, 243, 119};	
public static int[] PANTONE_375C_L3			= {193, 231, 112};
public static int[] PANTONE_375C_MAIN		= {143, 212, 0};
public static int[] PANTONE_376C_D3			= {119, 184, 0};
public static int[] PANTONE_377C_D2			= {113, 149, 0};
public static int[] PANTONE_378C_D1			= {84, 95, 29};	*/

// 130C		
/*public static int[] PANTONE_127C_L1			= {243, 222, 116};
public static int[] PANTONE_128C_L2			= {246, 214, 84};	
public static int[] PANTONE_129C_L3			= {245, 207, 71};
public static int[] PANTONE_130C_MAIN		= {244, 170, 0};
public static int[] PANTONE_131C_D3			= {169, 119, 0};
public static int[] PANTONE_132C_D2			= {164, 119, 0};
public static int[] PANTONE_133C_D1			= {110, 88, 25};*/	

/*// 2736C		
public static int[] PANTONE_2706C_L1			= {203, 209, 232};
public static int[] PANTONE_2716C_L2			= {157, 170, 226};	
public static int[] PANTONE_2726C_L3			= {72, 93, 197};
public static int[] PANTONE_2736C_MAIN			= {28, 41, 167};
public static int[] PANTONE_2746C_D3			= {26, 39, 145};
public static int[] PANTONE_2756C_D2			= {22, 33, 108};
public static int[] PANTONE_2766C_D1			= {23, 33, 84};*/

/*// Process Blue C		
public static int[] PANTONE_304C_L1				= {159, 221, 234};
public static int[] PANTONE_305C_L2				= {95, 206, 234};	
public static int[] PANTONE_306C_L3				= {0, 182, 227};
public static int[] PANTONE_ProcessBlueC_MAIN	= {0, 133, 207};
public static int[] PANTONE_307C_D3				= {0, 114, 177};
public static int[] PANTONE_308C_D2				= {0, 90, 132};
public static int[] PANTONE_309C_D1				= {0, 61, 77};*/