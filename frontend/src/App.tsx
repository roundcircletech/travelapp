import { useState } from 'react'
import Workflow from './components/Workflow'
import DashboardLayout from './components/DashboardLayout'

function App() {
  const [activeWorkflowId, setActiveWorkflowId] = useState<string | undefined>(undefined);

  return (
    <DashboardLayout
      onSelectWorkflow={setActiveWorkflowId}
      activeWorkflowId={activeWorkflowId}
    >
      <Workflow
        activeWorkflowId={activeWorkflowId}
        onWorkflowChange={setActiveWorkflowId}
      />
    </DashboardLayout>
  )
}

export default App
