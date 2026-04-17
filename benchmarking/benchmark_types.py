"""Shared type definitions for benchmarking scripts.

All enums, dataclasses, and type-helper functions are centralised here
so that build.py, run.py and their consumers can rely on a single
source of truth for the domain model.
"""

from __future__ import annotations

from dataclasses import dataclass
from enum import Enum, auto
from typing import Optional


class ExecutableType(Enum):
    Jar = "Jar"
    Node = "Node"
    Exe = "Exe"


class GameType(Enum):
    CommonMain = "CommonMain"
    DedicatedMain = "DedicatedMain"


@dataclass
class KPerfConfig:
    enabled: bool
    flush_early: bool
    instrument_property_accessors: bool
    test_kir: bool
    methods: str


@dataclass
class BenchmarkExecutable:
    name: str
    path: str
    type: ExecutableType
    config: Optional[KPerfConfig]  # None for reference (uninstrumented) executables


@dataclass
class ConfidenceInterval95:
    lower: float
    upper: float


@dataclass
class BenchmarkStatistics:
    count: int
    mean: Optional[float] = None
    median: Optional[float] = None
    stddev: Optional[float] = None
    min: Optional[float] = None
    max: Optional[float] = None
    ci95: Optional[ConfidenceInterval95] = None


def get_game_type_string(game_type: GameType) -> str:
    if game_type == GameType.CommonMain:
        return "commonmain"
    elif game_type == GameType.DedicatedMain:
        return "dedicatedmain"
    raise ValueError(f"Unknown GameType: {game_type}")
