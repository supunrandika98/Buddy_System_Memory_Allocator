/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package buddysystemmemorymanagement;

/**
 *
 * @author user
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class BuddySystem {

    // Memory blocks stored as pairs (blockSize, isAllocated, data)
    private List<Block> memoryBlocks = new ArrayList<>();

    public static void main(String[] args) {
        BuddySystem buddySystem = new BuddySystem();
        buddySystem.initializeMemory();
        buddySystem.start();
    }

    // Block class to represent a memory block with data
    private static class Block {

        int size;
        boolean isAllocated;
        String data;

        Block(int size, boolean isAllocated, String data) {
            this.size = size;
            this.isAllocated = isAllocated;
            this.data = data;
        }
    }

    // Initialize memory blocks
    public void initializeMemory() {
        // Add the initial large block (1024 KB), marked as free (false)
        memoryBlocks.add(new Block(1024, false, null));
    }

    // Main menu
    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nBuddy System Memory Management:");
            System.out.println("1. Allocate memory");
            System.out.println("2. Deallocate memory");
            System.out.println("3. Exit");

            System.out.print("\nEnter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("\nEnter the block size to allocate: ");
                    int allocateSize = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter the allocation data (e.g., A): ");
                    String allocationData = scanner.nextLine();
                    allocateMemory(allocateSize, allocationData);
                    break;
                case "2":
                    System.out.print("\nEnter the block size to deallocate: ");
                    int deallocateSize = Integer.parseInt(scanner.nextLine());
                    deallocateMemory(deallocateSize);
                    break;
                case "3":
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice! Please enter again.");
                    break;
            }
        }
    }

    // Allocate memory based on the Buddy System
    public void allocateMemory(int size, String data) {
        // Find the smallest power of 2 greater than or equal to the requested size
        int blockSize = findNextPowerOfTwo(size);

        // Try to allocate memory
        if (canAllocate(blockSize)) {
            allocate(blockSize, data);
            System.out.println("\nAllocated " + blockSize + " KB with data '" + data + "' successfully.");
            displayMemoryStatus();  // Display memory status after allocation
        } else {
            System.out.println("\nInsufficient memory for allocation.");
        }
    }

    // Find the next power of 2 greater than or equal to the requested size
    public int findNextPowerOfTwo(int size) {
        int power = 1;
        while (power < size) {
            power *= 2;
        }
        return power;
    }

    // Check if a block of the given size can be allocated
    public boolean canAllocate(int blockSize) {
        for (Block block : memoryBlocks) {
            if (!block.isAllocated && block.size >= blockSize) {
                return true;
            }
        }
        return false;
    }

    // Allocate memory by splitting blocks
    public void allocate(int blockSize, String data) {
        // Try to find a free block that can be split
        for (int i = 0; i < memoryBlocks.size(); i++) {
            Block block = memoryBlocks.get(i);
            if (!block.isAllocated && block.size >= blockSize) {
                // If the block is large enough, split it until we get the desired block size
                splitBlock(i, blockSize);
                memoryBlocks.get(i).isAllocated = true;  // Mark the block as allocated
                memoryBlocks.get(i).data = data;  // Store the allocation data
                return;
            }
        }
    }

    // Recursively split blocks until the size matches the requested block size
    public void splitBlock(int index, int blockSize) {
        int currentSize = memoryBlocks.get(index).size;

        // If the block size is larger than the requested size, split it into two smaller blocks
        while (currentSize > blockSize) {
            currentSize /= 2;
            memoryBlocks.add(index, new Block(currentSize, false, null));  // Add the new free block
            memoryBlocks.set(index + 1, new Block(currentSize, false, null));  // Update the other block
        }
    }

    // Deallocate memory
    public void deallocateMemory(int size) {
        for (int i = 0; i < memoryBlocks.size(); i++) {
            Block block = memoryBlocks.get(i);
            if (block.size == size && block.isAllocated) {
                // Mark the block as free
                block.isAllocated = false;
                block.data = null;  // Clear the data
                System.out.println("\nDeallocated " + size + " KB successfully.");
                // Try to merge the free blocks
                mergeBlocks();
                displayMemoryStatus();  // Display memory status after deallocation
                return;
            }
        }
        System.out.println("\nBlock not found for deallocation.");
    }

    // Merge adjacent free blocks to reduce fragmentation
    public void mergeBlocks() {
        for (int i = 0; i < memoryBlocks.size() - 1; i++) {
            Block current = memoryBlocks.get(i);
            Block next = memoryBlocks.get(i + 1);
            if (!current.isAllocated && !next.isAllocated && current.size == next.size) {
                // Merge the blocks
                memoryBlocks.set(i, new Block(current.size * 2, false, null));
                memoryBlocks.remove(i + 1);
                i--; // Check the previous block again
            }
        }
    }

    // Display the current memory status
    public void displayMemoryStatus() {
        System.out.println("\nMemory State:");

        for (Block block : memoryBlocks) {
            if (block.isAllocated) {
                System.out.println(block.size + " KB (allocated) - Data: " + block.data);
            } else {
                System.out.println(block.size + " KB (free)");
            }
        }
    }
}
