"""Call graph visualizer for performance trace analysis."""

import argparse
import json
import sys
from pathlib import Path
from typing import Dict

try:
    from graphviz import Source
except ImportError:
    Source = None


class Method:
    """Represents a method in the call graph with timing and call information."""

    def __init__(self, method_id: str, name: str, called_from: 'Method') -> None:
        """Initialize a Method object.

        Args:
            method_id: Unique identifier for the method
            name: Method name
            called_from_id: The method ID from which this method was called"""
        self.id = method_id
        self.name = name
        self.called_from: 'Method' = called_from
        self.n_called = 0
        self.durations: list[int] = []
        self.calls_to: Dict[str, 'Method'] = {}

    def add_call(self, to_method: 'Method') -> None:
        """Record a call to another method.

        Args:
            to_method: The method being called
        """
        if to_method.id in self.calls_to:
            self.calls_to[to_method.id].n_called += 1
        else:
            to_method.n_called = 1
            self.calls_to[to_method.id] = to_method

    def micros(self) -> int:
        """Return total duration in microseconds."""
        return sum(self.durations)

    def millis(self) -> float:
        """Return total duration in milliseconds."""
        return self.micros() / 1000.0

    def identifier(self) -> str:
        """Return identifier."""
        return f"{self.id}from{self.called_from.identifier()}" if self.called_from else self.id

    def label(self, overall_time) -> str:
        """Return string representation of the method."""
        return f"{self.name.split('.')[-1]} (~{self.millis():.2f}ms -- {self.millis() / overall_time * 100:.1f}%)"

    def n_calls_recursive(self) -> int:
        """Return total number of calls recursively."""
        total = self.n_called
        for called_method in self.calls_to.values():
            total += called_method.n_calls_recursive()
        return total


def build_aggregated_call_graph(file_path: Path, symbols: Dict[str, str]) -> Method:
    """Read a trace file and build an aggregated call graph.

    Args:
        file_path: Path to the trace file
        symbols: Dictionary mapping method IDs to method names

    Returns:
        Root system method containing the call graph
    """
    system_method = Method(str(sys.maxsize), "system", None)
    method_stack = [system_method]

    def current_method() -> Method:
        return method_stack[-1]

    with open(file_path, 'r') as file:
        for line in file:
            line = line.strip()
            if line.startswith(">"):
                method_id = line[2:]
                # Build a new method object if we do not have one yet for this ID
                if method_id not in current_method().calls_to:
                    called_method = Method(method_id, symbols.get(
                        method_id, "method name not found in symbols file for method " + method_id), current_method())
                else:
                    called_method = current_method().calls_to[method_id]
                # Record the method call ...
                current_method().add_call(called_method)
                # ... and "enter" the method
                method_stack.append(called_method)
            elif line.startswith("<"):
                method_id, duration = line[2:].split(';')
                # Remember how long the method ran ...
                current_method().durations.append(int(duration))
                # ... and "exit" the method
                method_stack.pop()
            else:
                print(
                    f"Warning: Skipping invalid line: {line}", file=sys.stderr)

    return system_method


def color_percentage(hex_color: str, percentage: float) -> str:
    """Calculate the hex color code with alpha channel for a given percentage.

    Args:
        hex_color: The base color in hex format (e.g., "#FF0000")
        percentage: The desired percentage (0-100)

    Returns:
        The hex color code with alpha channel
    """
    # Clamp percentage to valid range
    percentage = max(0, min(100, percentage))

    alpha = hex(int(255 * percentage / 100))[2:].zfill(2)
    return f"{hex_color}{alpha}"


def build_dot(method: Method, overall: int, lines: list, visited: set) -> None:
    """Build DOT format nodes and edges for a method recursively.

    Args:
        method: The method to process
        overall: Total duration for percentage calculation
        lines: List to append DOT lines to
        visited: Set of visited method IDs to avoid duplicates
    """
    caller_id = method.identifier()
    caller_label = method.label(overall)

    # Add node if not already visited
    if caller_id not in visited:
        visited.add(caller_id)
        node_color = color_percentage(
            "#FF9797", method.millis() / overall * 100)
        node_string = f'"{caller_id}"[label="{caller_label}", fillcolor="{node_color}"];'
        lines.append(node_string)

    # Add edges for called methods
    for called_method in method.calls_to.values():
        callee_id = called_method.identifier()
        # Recursively build for called method
        build_dot(called_method, overall, lines, visited)
        # Add edge to called method
        lines.append(
            f'"{caller_id}" -> "{callee_id}"[label="{called_method.n_called:,} (+ {called_method.n_calls_recursive() - called_method.n_called:,} indir.)"];')


def build_dot_graph(method: Method, overall: int) -> str:
    """Build complete DOT graph string.

    Args:
        method: Root method of the call graph
        overall: Total duration for percentage calculation

    Returns:
        Complete DOT graph as a string
    """
    lines = ["digraph G { node[shape=box,style=filled];"]
    visited = set()
    build_dot(method, overall, lines, visited)
    lines.append("}")
    return "\n".join(lines)


def main() -> None:
    """Main entry point for the call graph visualizer."""
    parser = argparse.ArgumentParser(
        description="Generate DOT graph visualization from performance trace files"
    )
    parser.add_argument(
        "trace_file",
        type=Path,
        help="Path to the trace file (e.g., trace.txt)"
    )
    parser.add_argument(
        "--symbols",
        type=Path,
        help="Path to the symbols file (defaults to trace file with 'symbols' in name)"
    )
    parser.add_argument(
        "-o", "--output",
        type=Path,
        help="Output PNG file path (defaults to trace file with .png extension)"
    )

    args = parser.parse_args()

    # Validate trace file exists
    if not args.trace_file.exists():
        print(
            f"Error: Trace file not found: {args.trace_file}", file=sys.stderr)
        sys.exit(1)

    # Determine symbols file path
    symbols_path = args.symbols
    if symbols_path is None:
        symbols_path = args.trace_file.parent / \
            args.trace_file.name.replace("trace", "symbols")

    # Validate symbols file exists
    if not symbols_path.exists():
        print(
            f"Error: Symbols file not found: {symbols_path}", file=sys.stderr)
        sys.exit(1)

    # Load symbols
    with open(symbols_path, 'r') as f:
        symbols = json.load(f)

    # Build call graph
    system_node = build_aggregated_call_graph(args.trace_file, symbols)

    # Get main method and generate DOT output
    if not system_node.calls_to:
        print("Error: No methods found in trace file", file=sys.stderr)
        sys.exit(1)

    main_method = next(iter(system_node.calls_to.values()))
    overall = main_method.millis()

    # Build DOT graph string
    dot_string = build_dot_graph(main_method, overall)

    # Print DOT string to stdout
    print(dot_string)

    # Render to PNG if graphviz is available
    if Source is not None:
        # Determine output path
        output_path = args.output
        if output_path is None:
            output_path = args.trace_file.with_suffix('.png')
        else:
            output_path = output_path.with_suffix('.png')

        try:
            graph = Source(dot_string)
            graph.format = 'png'
            output_file = graph.render(filename=str(
                output_path.with_suffix('')), cleanup=True)
            print(f"\nCall graph saved to: {output_file}", file=sys.stderr)
        except Exception as e:
            print(f"\nWarning: Could not render PNG: {e}", file=sys.stderr)
    else:
        print("\nNote: Install graphviz to generate PNG output: pip install graphviz", file=sys.stderr)


if __name__ == "__main__":
    main()
