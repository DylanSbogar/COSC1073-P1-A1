// Dylan Sbogar: s3718036

package control;

import java.util.Arrays;

import robot.Robot;

//Robot Assignment for Programming 1 s1 2018
//Adapted by Caspar and Ross from original Robot code in written by Dr Charles Thevathayan
public class RobotControl implements Control {
	// we need to internally track where the arm is
	private int height = Control.INITIAL_HEIGHT;
	private int width = Control.INITIAL_WIDTH;
	private int depth = Control.INITIAL_DEPTH;

	private int[] barHeights;
	private int[] blockHeights;
	int maxBar, currentBlockHeight, blockSelected, blockCount;
	int threesPlaced = 0;
	int columnHeights[] = new int[] { 1, 2 };

	private Robot robot;

	// called by RobotImpl
	@Override
	public void control(Robot robot, int barHeightsDefault[], int blockHeightsDefault[]) {
		this.robot = robot;

		// some hard coded init values you can change these for testing
		this.barHeights = new int[] { 7, 3, 1, 7, 5, 3, 2 };
		this.blockHeights = new int[] { 3, 1, 2, 3, 1, 1, 1 };

		blockCount = blockHeights.length;

		// Initialize the robot
		robot.init(this.barHeights, this.blockHeights, height, width, depth);
		getBlockHeight();
		// count how many blocks are placed
		int blockCount = blockHeights.length;
		// this loop iterates once for every element of the blockHeights array
		for (int i = 0; i < blockHeights.length; i++) {
			// copy the contents of barHeights[] into a new array and sort
			int[] copiedBarHeights = barHeights.clone();
			Arrays.sort(copiedBarHeights);
			extendToWidth(Control.MAX_WIDTH);
			lowerDown(this.height - 1 - currentBlockHeight);
			robot.pick();
			blockSelected = identifySelectedBlock();
			// depending on the block that was picked (1/2 or 3)
			if (blockSelected == 1 || blockSelected == 2) {
				if (maxBar >= currentBlockHeight) {
					moveArmUp(maxBar + blockSelected + 1);
				}
				contractToWidth(Control.MIN_WIDTH + blockSelected - 1);
				lowerDown(this.height - columnHeights[blockSelected - 1] - 1);
				// increase the total height of the selected blocks column by its height
				columnHeights[blockSelected - 1] += blockSelected;
				robot.drop();
				currentBlockHeight -= blockSelected;
				maxBar = getMaxBarHeight();
				System.out.println("Debug: Max bar height is " + maxBar);

				// if the block picked is not 1 or 2, it must be 3.
			} else {
				moveArmUp(copiedBarHeights[copiedBarHeights.length - 1 - threesPlaced] + blockSelected + 1);
				contractToWidth(Control.FIRST_BAR_COLUMN + threesPlaced);
				lowerDown(this.height - this.barHeights[threesPlaced] - 1 - blockSelected);
				robot.drop();
				this.barHeights[threesPlaced] += 3;
				threesPlaced++;
				currentBlockHeight -= blockSelected;
				maxBar = getMaxBarHeight();
				System.out.println("Debug: Max bar height is " + maxBar);
			}
			resetArmDepth(Control.MIN_DEPTH);
			// if the height of the source column is taller than that of the highest column
			if (currentBlockHeight > maxBar) {
				moveArmDown(currentBlockHeight + 1);
			} else {
				moveArmDown(maxBar + 1);
			}
		}
	}
	private void extendToWidth(int width) {
		while (this.width < width) {
			robot.extend();
			this.width++;
		}
	}
	public void resetArmDepth(int depth) {
		while (this.depth > depth) {
			robot.raise();
			this.depth--;
		}
	}
	private void getBlockHeight() {
		for (int i : blockHeights) {
			currentBlockHeight += i;
		}
	}
	private void lowerDown(int depth) {
		while (this.depth < depth) {
			robot.lower();
			this.depth++;
		}
	}
	private int identifySelectedBlock() {
		int selectedBlock = blockHeights[blockCount - 1];
		blockCount--;
		return selectedBlock;
	}
	private void contractToWidth(int width) {
		resetArmDepth(Control.MIN_DEPTH);
		while (this.width > width) {
			robot.contract();
			this.width--;
		}
	}
	private int getMaxBarHeight() {
		int maxBarHeight = barHeights[0];
		for (int i = 0; i < barHeights.length; i++) {
			if (barHeights[i] > maxBarHeight) {
				maxBarHeight = barHeights[i];
			}
			if (columnHeights[0] - 1 > maxBarHeight) {
				maxBarHeight = columnHeights[0];
			}
			if (columnHeights[1] - 2 > maxBarHeight) {
				maxBarHeight = columnHeights[1];
			}
		}
		return maxBarHeight;
	}
	private void moveArmUp(int height) {
		while (this.height < height) {
			robot.up();
			this.height++;
		}
	}
	private void moveArmDown(int height) {
		while (this.height > height) {
			robot.down();
			this.height--;
		}
	}
}
