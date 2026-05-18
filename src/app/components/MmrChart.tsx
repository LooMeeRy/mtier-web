"use client";

import { 
  LineChart, 
  Line, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  ResponsiveContainer 
} from "recharts";

interface MmrChartProps {
  matches: {
    createdAt: Date;
    mmrChange: number;
  }[];
  initialMmr: number;
}

export default function MmrChart({ matches, initialMmr }: MmrChartProps) {
  // Process matches into a chronological timeline
  const sortedMatches = [...matches].sort((a, b) => 
    new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
  );

  let currentMmr = initialMmr - sortedMatches.reduce((acc, m) => acc + m.mmrChange, 0);
  
  const data = sortedMatches.map((m) => {
    currentMmr += m.mmrChange;
    return {
      date: new Date(m.createdAt).toLocaleDateString(undefined, { month: 'short', day: 'numeric' }),
      MMR: currentMmr,
    };
  });

  if (data.length === 0) {
    return (
      <div className="h-full flex items-center justify-center text-zinc-800 font-black uppercase tracking-[0.3em] text-[10px]">
        Insufficient Data Points
      </div>
    );
  }

  return (
    <div className="w-full h-full min-h-[180px]">
      <ResponsiveContainer width="100%" height="100%">
        <LineChart data={data}>
          <XAxis 
            dataKey="date" 
            axisLine={false} 
            tickLine={false} 
            tick={{ fill: '#3f3f46', fontSize: 10, fontWeight: 900 }}
            dy={10}
          />
          <YAxis 
            hide={true} 
            domain={['dataMin - 100', 'dataMax + 100']}
          />
          <Tooltip 
            contentStyle={{ 
              backgroundColor: '#121214', 
              border: '1px solid #27272a',
              borderRadius: '8px',
              fontSize: '12px',
              fontWeight: '900',
              color: '#fff'
            }}
            itemStyle={{ color: '#f97316' }}
            cursor={{ stroke: '#f97316', strokeWidth: 1, strokeDasharray: '4 4' }}
          />
          <Line 
            type="monotone" 
            dataKey="MMR" 
            stroke="#f97316" 
            strokeWidth={4} 
            dot={{ r: 4, fill: '#f97316', strokeWidth: 0 }}
            activeDot={{ r: 6, fill: '#fff', stroke: '#f97316', strokeWidth: 2 }}
            animationDuration={1500}
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}
