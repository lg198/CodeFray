package com.github.lg198.codefray.controllers;

import com.github.lg198.codefray.api.game.Team;
import com.github.lg198.codefray.api.game.TileType;
import com.github.lg198.codefray.api.golem.*;
import com.github.lg198.codefray.api.math.Direction;
import com.github.lg198.codefray.api.math.Point;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Stream;

@ControllerDef(
        id = "com.github.lg198.AggressiveOffense",
        name = "CodeFray v1 Included - AggressiveOffense",
        version = "1.0",
        devId = "default")
/**
 * This controller features assault and runner golems who rush the other team.
 * The defenders wander randomly, searching for members of the other team.
 */
public class AggressiveOffenseController implements GolemController {

    public static Team thisTeam, thatTeam;
    public static boolean initialized = false;

    public static int golemWithFlag = -1;
    public Map<Integer, Deque<Direction>> paths = new HashMap<>();

    @Override
    public void onRound(Golem g) {
        try {
            if (!initialized) {
                initialized = true;
                thisTeam = g.getTeam();
                thatTeam = Team.otherTeam(thisTeam);
            }

            if (g.getType() == GolemType.DEFENDER) {
                onRoundDefender(g);
            } else {
                onRoundOffense(g);
            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.close();
            Logger.getLogger(AggressiveOffenseController.class.getSimpleName()).severe(sw.toString());
        }
    }

    private void onRoundDefender(Golem g) {
        shootAsManyAsPossible(g);
        while (g.getMovesLeft() > 0) {
            randomDirectionStream().forEach(d -> {
                if (g.canMove(d)) {
                    g.move(d);
                }
            });
        }
    }

    private void shootAsManyAsPossible(Golem g) {
        List<GolemInfo> search = g.search();
        Collections.sort(search, (o1, o2) -> manhattanDistance(g.getLocation(), o1.getLocation()) - manhattanDistance(g.getLocation(), o2.getLocation()));
        if (search.size() > 0) {
            for (GolemInfo gi : cyclicIterator(search)) {
                if (g.getShotsLeft() < 1) {
                    break;
                }
                g.shoot(gi);
            }
        }
    }

    private void onRoundOffense(Golem g) {
        if (g.getId() == golemWithFlag && !g.isHoldingFlag()) {
            golemWithFlag = -1;
            paths.clear();
        }
        if (golemWithFlag > -1) { //someone has the flag
            if (golemWithFlag == g.getId()) { //you have flag
                if (!paths.containsKey(g.getId())) { //get the path home!
                    initPath(g, g.getGame().getWinLocation(thisTeam));
                }
                int waitCount = 0;
                while (g.getMovesLeft() > 0) {
                    if (g.getLocation().equals(g.getGame().getWinLocation(thisTeam))) { //reached win
                        System.out.println(g.getId() + " has won the game... it thinks. ");
                        break;
                    }
                    Direction d = paths.get(g.getId()).pollFirst();
                    if (g.canMove(d)) g.move(d);
                    else { //new path!
                        if (++waitCount > 5) {
                            break;
                        }
                        initPath(g, g.getGame().getWinLocation(thisTeam));
                    }

                }
            } else { //its someone else
                onRoundDefender(g); //pretend its a defender
            }
        } else { //still looking for the flag
            if (!paths.containsKey(g.getId())) { //gotta generate the path to the flag!
                initPath(g, g.getGame().getFlagLocation(thatTeam));
            }
            int waitCount = 0;
            while (g.getMovesLeft() > 0) {
                if (g.getLocation().equals(g.getGame().getFlagLocation(thatTeam))) { //reached flag
                    System.out.println(g.getId() + " has picked up the flag... it thinks. " + g.isHoldingFlag());
                    golemWithFlag = g.getId();
                    paths.clear();
                    break;
                }
                Direction d = paths.get(g.getId()).pollFirst();
                if (g.canMove(d)) g.move(d);
                else {
                   if (++waitCount > 5) {
                       break;
                   }
                    initPath(g, g.getGame().getFlagLocation(thatTeam));
                }
            }
        }
    }

    private void initPath(Golem g, Point end) {
        Queue<Point> frontier = new LinkedList<>();
        frontier.add(g.getLocation());
        Map<Point, Point> cameFrom = new HashMap<>();
        cameFrom.put(g.getLocation(), null);

        while (!frontier.isEmpty()) {
            Point current = frontier.poll();

            if (current.equals(end)) break;

            Arrays.stream(Direction.values())
                    .filter(d -> current.in(d).inBounds(0, g.getGame().getMapWidth(), 0, g.getGame().getMapHeight()))
                    .map(d -> current.in(d))
                    .filter(p -> g.getGame().getTypeAt(p) != TileType.WALL)
                    .filter(p -> {
                        for (GolemInfo gi : g.search()) {
                            if (gi.getLocation().equals(p)) {
                                return false;
                            }
                        }
                        return true;
                    })
                    .forEach(next -> {
                        if (!cameFrom.containsKey(next)) {
                            frontier.add(next);
                            cameFrom.put(next, current);
                        }
                    });
        }

        Point current = end;
        List<Point> path = new ArrayList<>();
        path.add(current);
        while (!current.equals(g.getLocation())) {
            current = cameFrom.get(current);
            path.add(current);
        }
        Collections.reverse(path);
        LinkedList<Direction> dirpath = new LinkedList<>();
        for (int i = 0; i < path.size() - 1; i++) {
            Direction d = Direction.between(path.get(i), path.get(i + 1));
            dirpath.add(d);
        }
        paths.put(g.getId(), dirpath);
    }

    private Stream<Direction> randomDirectionStream() {
        List l = Arrays.asList(Arrays.copyOf(Direction.values(), Direction.values().length));
        Collections.shuffle(l);
        return l.stream();
    }

    private int manhattanDistance(Point a, Point b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    private <T> Iterable<T> cyclicIterator(List<T> l) {
        Iterable<T> i = new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {

                    private int current = 0;

                    @Override
                    public boolean hasNext() {
                        return true;
                    }

                    @Override
                    public T next() {
                        if (current >= l.size()) {
                            current = 0;
                        }
                        return l.get(current++);
                    }
                };
            }

            @Override
            public Spliterator<T> spliterator() {
                return new Spliterator<T>() {

                    private int current = 0;

                    @Override
                    public boolean tryAdvance(Consumer<? super T> action) {
                        if (current + 1 >= l.size()) {
                            action.accept(l.get(0));
                        } else {
                            action.accept(l.get(current + 1));
                        }
                        return true;
                    }

                    @Override
                    public Spliterator<T> trySplit() {
                        return null;
                    }

                    @Override
                    public long estimateSize() {
                        return 0;
                    }

                    @Override
                    public int characteristics() {
                        return 0;
                    }
                };
            }

            @Override
            public void forEach(Consumer<? super T> action) {
                l.forEach(action);
            }
        };
        return i;
    }
}
